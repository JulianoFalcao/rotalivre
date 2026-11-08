# Rota Livre — Evidências da versão 0.1.0

Execuções de 07/11/2026 sobre dados sintéticos, sem participantes humanos nem vistorias territoriais.

## Testes Java

`mvn package`: **31 testes**, **0 falhas**, **0 erros**, build aprovado. Dois grupos: 11 testes do algoritmo e 20 testes da API/banco/segurança. Cobertura inclui caminho mínimo, desvio, dados desconhecidos/antigos/futuros, degraus, largura, inclinação, direção, origem igual, ponto ausente, HTTP 400/401/403/404/409, CSRF, múltiplos bloqueios, histórico, login por senha válida/inválida e indicadores sem registros. Não se afirma percentual de cobertura de linhas.

## Navegação real

`testar_interface.py`: **9 verificações** aprovadas em Chromium. Percurso sintético **360 → 390 → 360 m**; relato bloqueante inserido via tela e JDBC, seguido de revisão pelo moderador, consulta do histórico e indicadores. Layout mobile de 390 px sem overflow horizontal da página e sem erro JavaScript na navegação examinada. O teste resolveu o relato de E2E e preservou o registro/histórico, não apagou dados.

O aumento de 30 m equivale a 8,33% da referência de 360 m; isso descreve a fixture, não um trajeto físico nem impacto medido para cidadãos. O tempo de cálculo varia por execução e não constitui benchmark ou tempo de viagem.

## Persistência

Após a execução E2E: **3 relatos**, IDs **33, 2 e 1**. Encerrado o servidor e reiniciado com o mesmo H2 em arquivo, o conteúdo das respostas de consulta dos relatos permaneceu igual, inclusive IDs e situações. O ID 33 é válido: lacunas de identidade não são contagem de registros. A fonte Java por si só não inclui esses dados locais; ao instalar o ZIP numa máquina nova, a fixture inicia com dois relatos.

## Acessibilidade digital

axe-core: **0 violações automáticas** nos escopos WCAG examinados das quatro telas. Duas verificações básicas de teclado: link de salto por Tab/Enter e diálogo por Enter/Escape. Existem regras inconclusivas que exigem revisão humana. Esse resultado não certifica conformidade WCAG, não substitui leitor de tela e não demonstra que todas as tarefas estejam plenamente acessíveis.

## Artefatos

Os resultados resumidos e capturas ficam em `docs/evidencias/` no pacote de código e documentação: `RESULTADOS_E2E.json`, `PERSISTENCIA_RESULTADO.json`, `ACESSIBILIDADE_AUTOMATICA.json` e telas de planejamento, relato, desvio, histórico, indicadores e transporte. Não foram incluídos logs de credenciais nem arquivos de banco/ambiente.

## Pendências

Avaliação com público-alvo, leitor de tela real, ampliação e teclado nas tarefas completas; dados georreferenciados e observados; integração autorizada SPTrans; administração de dados reais; ambiente de publicação permanente e sua segurança; vídeos comercial e técnico de até três minutos cada; links reais e submissão FIAP. ArboriGov não foi alterado.
