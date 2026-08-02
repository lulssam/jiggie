-- o trigger em auth.users não cai com os drop table
drop trigger if exists on_auth_user_created on auth.users;

drop table if exists passeio, agua, comida, sintoma,
  administracao_medicamento, medicamento, cao, dono, familia cascade;

drop function if exists
  current_familia_id(),
  join_familia(text),
  create_familia(text),
  leave_familia(),
  get_codigo_convite(),
  limpar_familias_orfas(int),
  handle_new_user(),
  set_familia_por_cao(),
  set_familia_por_medicamento(),
  propagar_familia()
cascade;