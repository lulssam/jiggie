#!/usr/bin/env bash
#
# Teste ponta a ponta do schema do Jiggie contra um projecto Supabase.
#
#   ./testar_jiggie.sh
#
# Pré-requisitos no projecto de teste:
#   - o ficheiro jiggie_schema.sql aplicado
#   - Authentication -> Providers -> Email -> "Confirm email" DESLIGADO
#
# Usa emails com timestamp, portanto é repetível sem apagar utilizadores.

set -u

# ---------------------------------------------------------------------------
# CONFIGURAÇÃO — preenche estas duas (Project Settings -> API)
# ---------------------------------------------------------------------------
SUPA_URL="${SUPA_URL:-}"
SUPA_KEY="${SUPA_KEY:-}"

if [[ -z "$SUPA_URL" || -z "$SUPA_KEY" ]]; then
  echo "Falta configurar. Corre primeiro:"
  echo '  export SUPA_URL="https://xxxxx.supabase.co"'
  echo '  export SUPA_KEY="<anon key>"'
  exit 1
fi

STAMP=$(date +%s)
PASS=0
FAIL=0

# ---------------------------------------------------------------------------
# helpers
# ---------------------------------------------------------------------------
jget() {  # jget campo [campo...]  — lê stdin, navega o JSON, imprime
  python3 -c '
import sys, json
try:
    d = json.load(sys.stdin)
except Exception:
    print(""); sys.exit(0)
for k in sys.argv[1:]:
    try:
        d = d[int(k)] if k.lstrip("-").isdigit() else d[k]
    except Exception:
        print(""); sys.exit(0)
print(d if d is not None else "")
' "$@"
}

ok()   { PASS=$((PASS+1)); printf "  \033[32m✓\033[0m %s\n" "$1"; }
bad()  { FAIL=$((FAIL+1)); printf "  \033[31m✗\033[0m %s\n" "$1"; }
head_() { printf "\n\033[1m%s\033[0m\n" "$1"; }

signup() {  # signup <email> <nome>  — imprime "token id"
  local r
  r=$(curl -s -X POST "$SUPA_URL/auth/v1/signup" \
        -H "apikey: $SUPA_KEY" -H "Content-Type: application/json" \
        -d "{\"email\":\"$1\",\"password\":\"teste1234\",\"data\":{\"nome\":\"$2\"}}")
  echo "$(echo "$r" | jget access_token) $(echo "$r" | jget user id)"
}

rpc() {  # rpc <token> <funcao> <json>
  curl -s -X POST "$SUPA_URL/rest/v1/rpc/$2" \
    -H "apikey: $SUPA_KEY" -H "Authorization: Bearer $1" \
    -H "Content-Type: application/json" -d "$3"
}

get() {  # get <token> <path>
  curl -s "$SUPA_URL/rest/v1/$2" \
    -H "apikey: $SUPA_KEY" -H "Authorization: Bearer $1"
}

post() {  # post <token> <tabela> <json>
  curl -s -X POST "$SUPA_URL/rest/v1/$2" \
    -H "apikey: $SUPA_KEY" -H "Authorization: Bearer $1" \
    -H "Content-Type: application/json" -H "Prefer: return=representation" -d "$3"
}


# ---------------------------------------------------------------------------
head_ "1. Signup — o trigger e o nome vindo do metadata"
# ---------------------------------------------------------------------------
read -r TOKEN_LU ID_LU <<< "$(signup "lu+$STAMP@teste.pt" "Lu")"

if [[ -z "$TOKEN_LU" ]]; then
  bad "signup falhou — sem access_token. Confirma que o 'Confirm email' está desligado."
  exit 1
fi
ok "signup devolveu sessão"

NOME=$(get "$TOKEN_LU" "dono?select=nome" | jget 0 nome)
if [[ "$NOME" == "Lu" ]]; then
  ok "handle_new_user gravou nome='Lu' vindo do metadata"
else
  bad "nome em dono é '$NOME', esperado 'Lu' — o raw_user_meta_data não chegou ao trigger"
fi


# ---------------------------------------------------------------------------
head_ "2. create_familia"
# ---------------------------------------------------------------------------
R=$(rpc "$TOKEN_LU" create_familia '{"p_nome":"Família Teste"}')
FAM_ID=$(echo "$R" | jget 0 familia_id)
CODIGO=$(echo "$R" | jget 0 codigo_convite)

if [[ -n "$FAM_ID" && -n "$CODIGO" ]]; then
  ok "família criada — código $CODIGO"
else
  bad "create_familia falhou: $R"
  exit 1
fi

MSG=$(rpc "$TOKEN_LU" create_familia '{"p_nome":"Outra"}' | jget message)
[[ "$MSG" == "Já pertences a uma família" ]] \
  && ok "segunda chamada rejeitada com a mensagem certa" \
  || bad "esperava 'Já pertences a uma família', veio: '$MSG'"


# ---------------------------------------------------------------------------
head_ "3. join_familia"
# ---------------------------------------------------------------------------
read -r TOKEN_MAE ID_MAE <<< "$(signup "mae+$STAMP@teste.pt" "Mãe")"

MSG=$(rpc "$TOKEN_MAE" join_familia '{"codigo":"XXXXXX"}' | jget message)
[[ "$MSG" == "Código de convite inválido" ]] \
  && ok "código inválido rejeitado" \
  || bad "esperava 'Código de convite inválido', veio: '$MSG'"

R=$(rpc "$TOKEN_MAE" join_familia "{\"codigo\":\"$CODIGO\"}")
[[ "$R" == "\"$FAM_ID\"" ]] \
  && ok "entrou na família da Lu" \
  || bad "join_familia devolveu: $R"

MSG=$(rpc "$TOKEN_MAE" join_familia "{\"codigo\":\"$CODIGO\"}" | jget message)
[[ "$MSG" == "Já pertences a uma família" ]] \
  && ok "segunda entrada rejeitada" \
  || bad "esperava 'Já pertences a uma família', veio: '$MSG'"


# ---------------------------------------------------------------------------
head_ "4. Cão e passeio — o trigger do familia_id"
# ---------------------------------------------------------------------------
CAO_ID=$(post "$TOKEN_LU" cao "{\"nome\":\"Bolinhas\",\"familia_id\":\"$FAM_ID\"}" | jget 0 id)
[[ -n "$CAO_ID" ]] && ok "cão criado" || { bad "não criou o cão"; exit 1; }

# familia_id forjado de propósito: o trigger tem de o esmagar
FORJADO="00000000-0000-0000-0000-000000000000"
R=$(post "$TOKEN_LU" passeio \
  "{\"cao_id\":\"$CAO_ID\",\"dono_id\":\"$ID_LU\",\"xixi\":true,\"familia_id\":\"$FORJADO\"}")
GRAVADO=$(echo "$R" | jget 0 familia_id)

if [[ "$GRAVADO" == "$FAM_ID" ]]; then
  ok "set_familia_por_cao esmagou o familia_id forjado"
elif [[ "$GRAVADO" == "$FORJADO" ]]; then
  bad "FALHA GRAVE: o familia_id forjado ficou gravado — o trigger não correu"
else
  bad "passeio não foi criado: $R"
fi


# ---------------------------------------------------------------------------
head_ "5. Isolamento entre famílias"
# ---------------------------------------------------------------------------
read -r TOKEN_EXT ID_EXT <<< "$(signup "ext+$STAMP@teste.pt" "Estranho")"
rpc "$TOKEN_EXT" create_familia '{"p_nome":"Família Alheia"}' > /dev/null

N=$(get "$TOKEN_EXT" "passeio?select=id" | python3 -c 'import sys,json;print(len(json.load(sys.stdin)))' 2>/dev/null || echo "?")
[[ "$N" == "0" ]] \
  && ok "estranho não vê nada ($N passeios)" \
  || bad "FALHA GRAVE: estranho vê $N passeios de outra família"

N=$(get "$TOKEN_MAE" "passeio?select=id" | python3 -c 'import sys,json;print(len(json.load(sys.stdin)))' 2>/dev/null || echo "?")
[[ "$N" == "1" ]] \
  && ok "membro da família vê o passeio da Lu" \
  || bad "membro da família devia ver 1 passeio, vê $N"


# ---------------------------------------------------------------------------
head_ "6. Privilégios de coluna"
# ---------------------------------------------------------------------------
R=$(curl -s -X PATCH "$SUPA_URL/rest/v1/dono?id=eq.$ID_LU" \
  -H "apikey: $SUPA_KEY" -H "Authorization: Bearer $TOKEN_LU" \
  -H "Content-Type: application/json" -H "Prefer: return=representation" \
  -d "{\"familia_id\":\"$FORJADO\"}")
CODE=$(echo "$R" | jget code)

[[ -n "$CODE" ]] \
  && ok "tentativa de mudar familia_id à mão bloqueada ($CODE)" \
  || bad "FALHA GRAVE: consegui mudar o familia_id directamente — o grant de coluna não pegou"


# ---------------------------------------------------------------------------
printf "\n\033[1m%d passaram, %d falharam\033[0m\n\n" "$PASS" "$FAIL"
[[ "$FAIL" -eq 0 ]] || exit 1