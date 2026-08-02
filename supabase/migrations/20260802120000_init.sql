-- =============================================================================
-- JIGGIE — schema completo
--
-- Fonte de verdade. Recria o projecto do zero, por esta ordem.
-- Validar com:  supabase db reset
-- =============================================================================


-- =============================================================================
-- 1. EXTENSÕES
-- =============================================================================

create extension if not exists pgcrypto;


-- =============================================================================
-- 2. TABELAS
-- =============================================================================

create table familia (
  id uuid primary key default gen_random_uuid(),
  nome text not null,
  codigo_convite text unique not null,
  codigo_expira_em timestamptz not null,
  orfa_desde timestamptz
);

create table dono (
  id uuid primary key references auth.users (id) on delete cascade,
  nome text not null,
  familia_id uuid references familia (id) on delete set null
);

create table cao (
  id uuid primary key default gen_random_uuid(),
  nome text not null,
  familia_id uuid not null references familia (id) on delete cascade
);

create table medicamento (
  id uuid primary key default gen_random_uuid(),
  nome text not null,
  cao_id uuid not null references cao (id) on delete cascade
);

create table administracao_medicamento (
  id uuid primary key default gen_random_uuid(),
  quantidade int not null,
  dh_medicamento timestamptz not null default now(),
  medicamento_id uuid not null references medicamento (id) on delete cascade,
  dono_id uuid not null references dono (id) on delete cascade,
  familia_id uuid not null references familia (id) on delete cascade
);

create table sintoma (
  id uuid primary key default gen_random_uuid(),
  dh_sintoma timestamptz not null default now(),
  tipo text not null,
  descricao text,
  cao_id uuid not null references cao (id) on delete cascade,
  dono_id uuid not null references dono (id) on delete cascade,
  familia_id uuid not null references familia (id) on delete cascade
);

create table comida (
  id uuid primary key default gen_random_uuid(),
  dh_comida timestamptz not null default now(),
  quantidade int not null,
  extras text,
  cao_id uuid not null references cao (id) on delete cascade,
  dono_id uuid not null references dono (id) on delete cascade,
  familia_id uuid not null references familia (id) on delete cascade
);

create table agua (
  id uuid primary key default gen_random_uuid(),
  dh_agua timestamptz not null default now(),
  quantidade int not null,
  cao_id uuid not null references cao (id) on delete cascade,
  dono_id uuid not null references dono (id) on delete cascade,
  familia_id uuid not null references familia (id) on delete cascade
);

create table passeio (
  id uuid primary key default gen_random_uuid(),
  dh_passeio timestamptz not null default now(),
  xixi boolean not null default false,
  coco boolean not null default false,
  cao_id uuid not null references cao (id) on delete cascade,
  dono_id uuid not null references dono (id) on delete cascade,
  familia_id uuid not null references familia (id) on delete cascade
);


-- =============================================================================
-- 3. ÍNDICES
-- =============================================================================

-- Listagens: "eventos desta família, mais recentes primeiro"
create index on passeio                   (familia_id, dh_passeio desc);
create index on comida                    (familia_id, dh_comida desc);
create index on agua                      (familia_id, dh_agua desc);
create index on sintoma                   (familia_id, dh_sintoma desc);
create index on administracao_medicamento (familia_id, dh_medicamento desc);

-- Chaves estrangeiras. O Postgres não as indexa sozinho, e sem índice
-- cada "on delete cascade" faz scan completo da tabela filha.
create index on dono        (familia_id);
create index on cao         (familia_id);
create index on medicamento (cao_id);

create index on passeio                   (cao_id);
create index on comida                    (cao_id);
create index on agua                      (cao_id);
create index on sintoma                   (cao_id);
create index on administracao_medicamento (medicamento_id);


-- =============================================================================
-- 4. FUNÇÕES
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Família do utilizador actual.
--
-- security definer: sem isto, ler a "dono" activa a policy de select da "dono",
--                   que chama esta função — recursão infinita.
-- stable:           o default é volatile, que a faria correr uma vez por linha.
-- -----------------------------------------------------------------------------
create or replace function current_familia_id()
returns uuid
language sql
security definer
stable
set search_path = public
as $$
  select familia_id from dono where id = auth.uid();
$$;


-- -----------------------------------------------------------------------------
-- Cria a linha em "dono" quando nasce um utilizador no auth.
-- O nome vem do metadata do signup.
-- -----------------------------------------------------------------------------
create or replace function handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  insert into dono (id, nome)
  values (new.id, coalesce(new.raw_user_meta_data->>'nome', 'Sem nome'));
  return new;
end;
$$;

create trigger on_auth_user_created
after insert on auth.users
for each row execute function handle_new_user();


-- -----------------------------------------------------------------------------
-- Criar família
-- -----------------------------------------------------------------------------
create or replace function create_familia(p_nome text)
returns table (familia_id uuid, codigo_convite text)
language plpgsql
security definer
set search_path = public, extensions
as $$
declare
  v_familia_id uuid;
  v_codigo text;
begin
  if auth.uid() is null then
    raise exception 'Utilizador não autenticado';
  end if;

  if exists (select 1 from dono d where d.id = auth.uid() and d.familia_id is not null) then
    raise exception 'Já pertences a uma família';
  end if;

  v_codigo := upper(substr(encode(gen_random_bytes(6), 'hex'), 1, 6));

  insert into familia (nome, codigo_convite, codigo_expira_em)
  values (p_nome, v_codigo, now() + interval '24 hours')
  returning id into v_familia_id;

  update dono d set familia_id = v_familia_id where d.id = auth.uid();

  if not found then
    raise exception 'Perfil de utilizador não encontrado — cria o teu perfil antes de criares uma família';
  end if;

  return query select v_familia_id, v_codigo;
end;
$$;


-- -----------------------------------------------------------------------------
-- Entrar numa família
-- -----------------------------------------------------------------------------
create or replace function join_familia(codigo text)
returns uuid
language plpgsql
security definer
set search_path = public
as $$
declare
  v_familia_id uuid;
  v_expira_em timestamptz;
begin
  if auth.uid() is null then
    raise exception 'Utilizador não autenticado';
  end if;

  if exists (select 1 from dono where id = auth.uid() and familia_id is not null) then
    raise exception 'Já pertences a uma família';
  end if;

  select id, codigo_expira_em into v_familia_id, v_expira_em
  from familia
  where codigo_convite = codigo;

  if v_familia_id is null then
    raise exception 'Código de convite inválido';
  end if;

  if v_expira_em < now() then
    raise exception 'Código de convite expirado';
  end if;

  update dono set familia_id = v_familia_id where id = auth.uid();

  -- O "if not found" tem de vir imediatamente a seguir ao update da "dono":
  -- qualquer statement pelo meio reescreve o FOUND e o guarda deixa de valer.
  if not found then
    raise exception 'Perfil de utilizador não encontrado — cria o teu perfil antes de te juntares a uma família';
  end if;

  -- Alguém voltou a entrar: a família já não está órfã.
  update familia set orfa_desde = null where id = v_familia_id;

  return v_familia_id;
end;
$$;


-- -----------------------------------------------------------------------------
-- Sair da família.
-- Se sair o último membro, marca como órfã em vez de apagar — apagar levaria
-- os cães e todo o histórico à frente, por cascade.
-- -----------------------------------------------------------------------------
create or replace function leave_familia()
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  v_familia_id uuid;
  v_restantes int;
begin
  if auth.uid() is null then
    raise exception 'Utilizador não autenticado';
  end if;

  select familia_id into v_familia_id from dono where id = auth.uid();

  if v_familia_id is null then
    raise exception 'Não pertences a nenhuma família';
  end if;

  update dono set familia_id = null where id = auth.uid();

  select count(*) into v_restantes from dono where familia_id = v_familia_id;

  if v_restantes = 0 then
    update familia set orfa_desde = now() where id = v_familia_id;
  end if;
end;
$$;


-- -----------------------------------------------------------------------------
-- Código de convite actual. Renova-o se tiver expirado.
-- -----------------------------------------------------------------------------
create or replace function get_codigo_convite()
returns table (codigo_convite text, codigo_expira_em timestamptz)
language plpgsql
security definer
set search_path = public, extensions
as $$
declare
  v_familia_id uuid;
  v_codigo text;
  v_expira timestamptz;
begin
  if auth.uid() is null then
    raise exception 'Utilizador não autenticado';
  end if;

  select familia_id into v_familia_id from dono where id = auth.uid();

  if v_familia_id is null then
    raise exception 'Não pertences a nenhuma família';
  end if;

  select f.codigo_convite, f.codigo_expira_em into v_codigo, v_expira
  from familia f where f.id = v_familia_id;

  if v_expira < now() then
    v_codigo := upper(substr(encode(gen_random_bytes(6), 'hex'), 1, 6));
    v_expira := now() + interval '24 hours';

    update familia set codigo_convite = v_codigo, codigo_expira_em = v_expira
    where id = v_familia_id;
  end if;

  return query select v_codigo, v_expira;
end;
$$;


-- -----------------------------------------------------------------------------
-- Limpeza de famílias órfãs. Corre por cron ou à mão — nunca pelo cliente.
-- -----------------------------------------------------------------------------
create or replace function limpar_familias_orfas(p_dias int default 30)
returns int
language plpgsql
security definer
set search_path = public
as $$
declare
  v_apagadas int;
begin
  delete from familia
  where orfa_desde is not null
    and orfa_desde < now() - make_interval(days => p_dias);

  get diagnostics v_apagadas = row_count;
  return v_apagadas;
end;
$$;


-- -----------------------------------------------------------------------------
-- Preenchimento do familia_id nas tabelas de evento.
--
-- O cliente nunca envia esta coluna (ver os grants de coluna mais abaixo).
-- O trigger é a única fonte do valor, o que garante que a RLS de leitura,
-- que confia nele, nunca mente.
-- -----------------------------------------------------------------------------
create or replace function set_familia_por_cao()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
  select c.familia_id into new.familia_id
  from public.cao c where c.id = new.cao_id;
  return new;
end;
$$;

create trigger trg_familia before insert or update on passeio
  for each row execute function set_familia_por_cao();
create trigger trg_familia before insert or update on comida
  for each row execute function set_familia_por_cao();
create trigger trg_familia before insert or update on agua
  for each row execute function set_familia_por_cao();
create trigger trg_familia before insert or update on sintoma
  for each row execute function set_familia_por_cao();


-- A administracao_medicamento não tem cao_id: chega à família pelo medicamento.
create or replace function set_familia_por_medicamento()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
  select c.familia_id into new.familia_id
  from public.medicamento m
  join public.cao c on c.id = m.cao_id
  where m.id = new.medicamento_id;
  return new;
end;
$$;

create trigger trg_familia before insert or update on administracao_medicamento
  for each row execute function set_familia_por_medicamento();


-- -----------------------------------------------------------------------------
-- Se um cão mudar de família, o familia_id dos eventos tem de o seguir.
-- Sem isto a RLS passa a mentir nos dois sentidos, em silêncio.
-- -----------------------------------------------------------------------------
create or replace function propagar_familia()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
  if new.familia_id is distinct from old.familia_id then
    update public.passeio set familia_id = new.familia_id where cao_id = new.id;
    update public.comida  set familia_id = new.familia_id where cao_id = new.id;
    update public.agua    set familia_id = new.familia_id where cao_id = new.id;
    update public.sintoma set familia_id = new.familia_id where cao_id = new.id;
    update public.administracao_medicamento a set familia_id = new.familia_id
      from public.medicamento m
      where m.id = a.medicamento_id and m.cao_id = new.id;
  end if;
  return new;
end;
$$;

create trigger trg_propagar after update on cao
  for each row execute function propagar_familia();


-- =============================================================================
-- 5. PRIVILÉGIOS DE EXECUÇÃO
--
-- O Postgres dá "execute" ao public por omissão. Revogar primeiro, depois
-- conceder só a authenticated, deixa o role anon de fora.
-- =============================================================================

revoke execute on function create_familia(text)        from public;
revoke execute on function join_familia(text)          from public;
revoke execute on function leave_familia()             from public;
revoke execute on function get_codigo_convite()        from public;
revoke execute on function current_familia_id()        from public;
revoke execute on function limpar_familias_orfas(int)  from public;

grant execute on function create_familia(text)  to authenticated;
grant execute on function join_familia(text)    to authenticated;
grant execute on function leave_familia()       to authenticated;
grant execute on function get_codigo_convite()  to authenticated;

-- current_familia_id precisa mesmo deste grant: as expressões das policies
-- correm com os privilégios de quem faz a query, não do owner. Sem ele,
-- qualquer select rebenta com "permission denied for function".
grant execute on function current_familia_id()  to authenticated;

-- limpar_familias_orfas fica sem grant nenhum — só postgres / service_role.


-- =============================================================================
-- 6. LIGAR RLS
--
-- "create table" não liga RLS. Sem estas linhas as policies abaixo são
-- decorativas e a base fica aberta a qualquer utilizador autenticado.
-- =============================================================================

alter table familia                   enable row level security;
alter table dono                      enable row level security;
alter table cao                       enable row level security;
alter table medicamento               enable row level security;
alter table administracao_medicamento enable row level security;
alter table sintoma                   enable row level security;
alter table comida                    enable row level security;
alter table agua                      enable row level security;
alter table passeio                   enable row level security;


-- =============================================================================
-- 7. PRIVILÉGIOS DE COLUNA
--
-- A RLS decide linhas, não colunas. É isto que impede o cliente de alterar
-- familia_id (entrar em qualquer família), dono_id (falsear autoria) e
-- codigo_convite / codigo_expira_em (contornar a expiração dos convites).
-- =============================================================================

revoke update on familia from authenticated;
grant  update (nome) on familia to authenticated;

revoke update on dono from authenticated;
grant  update (nome) on dono to authenticated;

revoke update on passeio from authenticated;
grant  update (dh_passeio, xixi, coco, cao_id) on passeio to authenticated;

revoke update on comida from authenticated;
grant  update (dh_comida, quantidade, extras, cao_id) on comida to authenticated;

revoke update on agua from authenticated;
grant  update (dh_agua, quantidade, cao_id) on agua to authenticated;

revoke update on sintoma from authenticated;
grant  update (dh_sintoma, tipo, descricao, cao_id) on sintoma to authenticated;

revoke update on administracao_medicamento from authenticated;
grant  update (dh_medicamento, quantidade, medicamento_id)
  on administracao_medicamento to authenticated;


-- =============================================================================
-- 8. POLICIES
--
-- Todas as chamadas vão embrulhadas em (select ...) para o planeador as
-- avaliar uma vez por query em vez de uma vez por linha.
-- =============================================================================

-- familia ---------------------------------------------------------------------
-- Sem insert: só via create_familia(). Sem delete: só via limpar_familias_orfas().
-- Ambas security definer, ignoram RLS.

create policy "select_familia" on familia for select
  using (id = (select current_familia_id()));

create policy "update_familia" on familia for update
  using (id = (select current_familia_id()))
  with check (id = (select current_familia_id()));


-- dono ------------------------------------------------------------------------

create policy "select_dono" on dono for select
  using (
    id = (select auth.uid())
    or familia_id = (select current_familia_id())
  );

-- familia_id null: entrar em família é só por create_familia / join_familia.
create policy "insert_dono" on dono for insert
  with check (id = (select auth.uid()) and familia_id is null);

create policy "update_dono" on dono for update
  using (id = (select auth.uid()))
  with check (id = (select auth.uid()));


-- cao -------------------------------------------------------------------------

create policy "select_cao" on cao for select
  using (familia_id = (select current_familia_id()));

create policy "insert_cao" on cao for insert
  with check (familia_id = (select current_familia_id()));

create policy "update_cao" on cao for update
  using (familia_id = (select current_familia_id()))
  with check (familia_id = (select current_familia_id()));

create policy "delete_cao" on cao for delete
  using (familia_id = (select current_familia_id()));


-- medicamento -----------------------------------------------------------------
-- Sem familia_id: não é tabela de realtime nem de listagem quente.
-- O join pela cao é barato e está indexado.

create policy "select_medicamento" on medicamento for select
  using (cao_id in (select id from cao where familia_id = (select current_familia_id())));

create policy "insert_medicamento" on medicamento for insert
  with check (cao_id in (select id from cao where familia_id = (select current_familia_id())));

create policy "update_medicamento" on medicamento for update
  using (cao_id in (select id from cao where familia_id = (select current_familia_id())))
  with check (cao_id in (select id from cao where familia_id = (select current_familia_id())));

create policy "delete_medicamento" on medicamento for delete
  using (cao_id in (select id from cao where familia_id = (select current_familia_id())));


-- tabelas de evento -----------------------------------------------------------
-- Leitura por familia_id: sem join, e filtrável do lado do servidor pelo Realtime.
-- Escrita valida o cao_id, que é o caminho de autorização verdadeiro.
--
-- O update não exige dono_id = auth.uid(): numa app de família faz sentido
-- corrigir o registo de outra pessoa. O que não pode mudar é a autoria, e disso
-- trata o grant de coluna acima.

-- passeio
create policy "select_passeio" on passeio for select
  using (familia_id = (select current_familia_id()));

create policy "insert_passeio" on passeio for insert
  with check (
    dono_id = (select auth.uid())
    and cao_id in (select id from cao where familia_id = (select current_familia_id()))
  );

create policy "update_passeio" on passeio for update
  using (familia_id = (select current_familia_id()))
  with check (cao_id in (select id from cao where familia_id = (select current_familia_id())));

create policy "delete_passeio" on passeio for delete
  using (familia_id = (select current_familia_id()));

-- comida
create policy "select_comida" on comida for select
  using (familia_id = (select current_familia_id()));

create policy "insert_comida" on comida for insert
  with check (
    dono_id = (select auth.uid())
    and cao_id in (select id from cao where familia_id = (select current_familia_id()))
  );

create policy "update_comida" on comida for update
  using (familia_id = (select current_familia_id()))
  with check (cao_id in (select id from cao where familia_id = (select current_familia_id())));

create policy "delete_comida" on comida for delete
  using (familia_id = (select current_familia_id()));

-- agua
create policy "select_agua" on agua for select
  using (familia_id = (select current_familia_id()));

create policy "insert_agua" on agua for insert
  with check (
    dono_id = (select auth.uid())
    and cao_id in (select id from cao where familia_id = (select current_familia_id()))
  );

create policy "update_agua" on agua for update
  using (familia_id = (select current_familia_id()))
  with check (cao_id in (select id from cao where familia_id = (select current_familia_id())));

create policy "delete_agua" on agua for delete
  using (familia_id = (select current_familia_id()));

-- sintoma
create policy "select_sintoma" on sintoma for select
  using (familia_id = (select current_familia_id()));

create policy "insert_sintoma" on sintoma for insert
  with check (
    dono_id = (select auth.uid())
    and cao_id in (select id from cao where familia_id = (select current_familia_id()))
  );

create policy "update_sintoma" on sintoma for update
  using (familia_id = (select current_familia_id()))
  with check (cao_id in (select id from cao where familia_id = (select current_familia_id())));

create policy "delete_sintoma" on sintoma for delete
  using (familia_id = (select current_familia_id()));

-- administracao_medicamento
create policy "select_administracao" on administracao_medicamento for select
  using (familia_id = (select current_familia_id()));

create policy "insert_administracao" on administracao_medicamento for insert
  with check (
    dono_id = (select auth.uid())
    and medicamento_id in (
      select m.id from medicamento m
      join cao c on c.id = m.cao_id
      where c.familia_id = (select current_familia_id())
    )
  );

create policy "update_administracao" on administracao_medicamento for update
  using (familia_id = (select current_familia_id()))
  with check (
    medicamento_id in (
      select m.id from medicamento m
      join cao c on c.id = m.cao_id
      where c.familia_id = (select current_familia_id())
    )
  );

create policy "delete_administracao" on administracao_medicamento for delete
  using (familia_id = (select current_familia_id()));


-- =============================================================================
-- 9. REALTIME
--
-- Vai no fim porque precisa das tabelas já criadas.
--
--   publicação      → quais as tabelas que emitem eventos (o interruptor)
--   replica identity → quanto da linha antiga vai no evento (a resolução)
--   RLS             → quem recebe o quê (o filtro; não se aplica a DELETE)
--
-- Com familia_id na tabela, o cliente subscreve com filter: familia_id=eq.<uuid>
-- =============================================================================

alter table passeio                   replica identity full;
alter table comida                    replica identity full;
alter table agua                      replica identity full;
alter table sintoma                   replica identity full;
alter table administracao_medicamento replica identity full;

alter publication supabase_realtime add table
  passeio, comida, agua, sintoma, administracao_medicamento;