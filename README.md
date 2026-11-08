# Rota Livre — piloto CitizenTech

**Juliano de Oliveira Falcão — RM 565907.** Versão inicial 0.1.0, 07/11/2026. Independente do ArboriGov SP.

Mobilidade com menos barreiras: consultar uma rota sobre um grafo sintético, registrar uma barreira no banco e recalcular; moderar o relato com histórico e examinar estatísticas SQL. Referência territorial: entorno do Terminal Santo Amaro, Zona Sul de São Paulo.

> Não é GPS, mapa cadastral ou serviço de mobilidade operacional. Os 12 pontos, 20 trechos, distâncias, atributos e relatos são de demonstração. Não utilizar os percursos para deslocamento real. Não há integração com SPTrans, veículo confirmado ou reserva Atende+.

## Executar localmente

Requisitos: **JDK 21**, **Maven 3.6.3+**, **Python 3** apenas para gerar credenciais pelo script e **Bash** para `run.sh`. Não requer conta em nuvem nem token SPTrans.

```bash
mvn test
mvn package
PORT=8085 ./run.sh
# Abrir http://localhost:8085
```

Na primeira execução, `run.sh` gera duas senhas aleatórias em `data/.credentials.env`, com permissão restrita. Não publicar esse arquivo. Contas: `cidadao` (CITIZEN) e `moderador` (ADMIN). Consultas públicas; inserções autenticadas; revisão somente ADMIN. Senhas são codificadas com BCrypt na memória. As contas são específicas para o demonstrador, não um cadastro de cidadãos reais.

Sem Bash/Python, configure `ROTA_CITIZEN_PASSWORD` e `ROTA_ADMIN_PASSWORD` com senhas diferentes de pelo menos 14 caracteres e execute:

```bash
java -Xmx256m -jar target/rota-livre-0.1.0.jar --server.port=8085
```

Não há senhas reais no fonte. As senhas presentes nos testes são exclusivas do ambiente de testes.

## Banco

H2 em arquivo: `data/rotalivre.mv.db`. JDBC, queries parametrizadas, transações e chaves estrangeiras. A inicialização idempotente só carrega fixtures quando a tabela de nós está vazia. Reiniciar não apaga os relatos nem seu histórico. O H2 Console está desabilitado. `src/main/resources/schema.sql` contém o modelo efetivo.

Para outro diretório: `ROTA_DB_URL=jdbc:h2:file:/caminho/rotalivre;DB_CLOSE_ON_EXIT=FALSE`. Não compartilhar um arquivo H2 aberto com múltiplos servidores. Banco e credenciais ficam excluídos do ZIP de fonte. A perda do diretório `data` não é resolvida por este demonstrador; implementar backup e restauração antes de operação real.

## Rotas e condições

Dijkstra com lista de adjacência e fila de prioridade (`RoutePlanner.java`). Custos de distância positivos. Complexidade `O((V+E) log V)` e memória `O(V+E)` no grafo do piloto; para multigrafos, o tamanho efetivo da fila também deve ser considerado. O conjunto de teste tem 20 arestas únicas e no máximo duas direções por trecho.

Excluir trechos bloqueados por relatos PENDING/CONFIRMED, dados desconhecidos, futuros ou com mais de 30 dias, degraus, piso fora da fixture REGULAR, largura abaixo do mínimo ou inclinação acima do máximo. Preferências são experimentais, não certificam cumprimento de normas de acessibilidade. `verified_at` no dataset é a data da fixture; não houve vistoria física.

A referência de comparação ignora relatos, mantendo a mesma rede e os mesmos critérios. Não é uma rota real mais curta da cidade. Resolução não reabilita trecho se outro relato ou outra restrição continuar ativo. Relatos finalizados não são reabertos por sobrescrita; uma ocorrência nova gera outro registro.

## API

| Método | Endpoint | Controle |
| --- | --- | --- |
| GET | `/api/health`, `/api/me`, `/api/csrf` | Público |
| GET | `/api/network` | Público, dados sintéticos |
| GET | `/api/route?from=1&to=8&minWidthCm=90&maxSlopePct=6` | Público, cálculo Java |
| GET | `/api/reports` | Público, até 200 recentes; sem autor |
| POST | `/api/reports` | Sessão e CSRF |
| PATCH | `/api/reports/{id}` | ADMIN e CSRF |
| GET | `/api/reports/{id}/history` | Sessão; sem identidade de autor |
| GET | `/api/transport`, `/api/stats` | Público, dados sintéticos |
| POST | `/login`, `/logout` | CSRF e sessão Spring Security |

Respostas relevantes: 200 consulta/atualização; 201 criação; 400 payload inválido; 401 sem sessão; 403 sem papel/CSRF; 404 recurso ausente; 409 situação já finalizada; 503 banco indisponível. Falha de banco não é apresentada como ausência de relatos. API usa validação Jakarta Bean Validation e erros sem stack trace/SQL exposto.

## Testes e evidências

- `mvn test`: algoritmo, HTTP, autenticação real, autorização, CSRF, validação, SQL e histórico.
- `scripts/testar_interface.py`: navegador real com criação de relato sintético e revisão. Requer Python/Playwright/Chromium e aplicação ativa. O teste cria um relato, resolve-o e preserva o histórico; não apaga dados.
- `scripts/testar_acessibilidade.py`: axe-core e duas verificações básicas de teclado; requer o caminho do `axe.min.js` em `AXE_SCRIPT`. Não certifica WCAG.
- `docs/EVIDENCIAS.md` e resultados JSON descrevem a execução observada. Validação humana e leitor de tela real permanecem pendentes.

```bash
ROTA_BASE_URL=http://127.0.0.1:8085 python3 scripts/testar_interface.py
AXE_SCRIPT=/caminho/axe-core/axe.min.js python3 scripts/testar_acessibilidade.py
```

## Frontend e documentação

HTML semântico, CSS responsivo e JavaScript sem framework, servidos pelo próprio Spring Boot. O navegador não calcula Dijkstra nem simula persistência: chama o Java. Diagrama esquemático em SVG tem alternativa textual; não utiliza uma rota de carro/pedestre como acessibilidade comprovada. Sem dados médicos, CPF, fotos ou histórico de geolocalização no formulário.

- `docs/ESPECIFICACAO_TECNICA.md`: público, problema, ODS, implementação e limitações.
- `docs/BACKLOG.md` e `docs/VALIDACAO_USUARIOS.md`: critérios, trabalho iterativo e avaliação planejada.
- `docs/ARQUITETURA.mmd`, `BANCO.mmd`, `FLUXO.mmd`, `CLASSES.mmd` e PNG: diagramas.
- `docs/WIREFRAMES.html`: esboços editáveis; `docs/WIREFRAMES.pdf`: versão para consulta.
- `docs/CONSULTAS_SQL.sql`: consultas reproduzíveis.

## Prévia e publicação

A prévia nesta sessão usa o Java na porta 8085 e o encaminhamento HTTPS temporário do sandbox; pode parar quando a sessão hibernar e não é domínio permanente. A criação de um segundo projeto gerenciado nesta conversa foi bloqueada. Não alteramos nem reutilizamos o projeto ArboriGov para contornar isso.

Antes de publicar um serviço real: ambiente independente compatível com Java, banco gerenciado ou volume durável, TLS, `ROTA_COOKIE_SECURE=true`, gestão de identidades, limites de requisições/login, monitoramento, backup/restauração, verificação de dados reais e avaliação com usuários. Contas compartilhadas e H2 de demonstração não são desenho de produção.

## Entrega acadêmica ainda a finalizar

Enunciado requer dois vídeos diferentes, no máximo três minutos cada: comercial sem implementação; técnico com Java, inserção/leitura/atualização, código e decisões. Seus links reais devem entrar no `.txt` do ZIP final. Não há vídeo publicado nem submissão FIAP deste projeto nesta etapa. Limite indicado: 19/11/2026, sem tolerância de três dias.
