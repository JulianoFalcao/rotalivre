# Rota Livre — Backlog e acompanhamento CitizenTech

**Juliano de Oliveira Falcão — RM 565907.** Atualização: 07/11/2026. Prazo do enunciado: 19/11/2026.

O projeto é independente do ArboriGov. Santo Amaro, na Zona Sul de São Paulo, é a referência territorial; a rede implementada tem **12 pontos e 20 trechos sintéticos**, sem georreferenciamento nem vistoria de ruas. Validação técnica não equivale a validação com o público.

## Priorização e estado observado

| ID | História | MoSCoW | Estado em 07/11/2026 | Evidência / pendência |
| --- | --- | --- | --- | --- |
| US01 | Consultar um percurso compatível com os critérios | Must | Validado tecnicamente na rede de teste | Dijkstra, testes unitários e percurso 360 → 390 → 360 m no navegador |
| US02 | Registrar uma barreira | Must | Núcleo validado tecnicamente | Inserção JDBC, leitura após recarga e reinício; aperfeiçoamento de associação dos erros de servidor aos campos permanece no backlog |
| US03 | Revisar o relato com histórico | Must | Validado tecnicamente | Moderação autorizada, histórico transacional e múltiplos bloqueios testados |
| US04 | Consultar transporte e serviços | Should | Listagem de demonstração validada; integração real a fazer | Dois exemplos em pontos da rede e orientação Atende+; sem previsão, reserva ou busca multimodal real |
| US05 | Interpretar registros e desvios | Must | Validado tecnicamente na base sintética | Agregações SQL, proteção de denominador zero, indicadores na interface |
| US06 | Usar a interface sem depender do esquema | Must | Em andamento | Lista textual, controles nativos, layout mobile, axe-core e duas verificações de teclado; faltam leitor de tela real, ampliação manual e avaliação completa das tarefas |

**Could:** fotografia com cuidados de privacidade, consulta de histórico mais detalhada, paginação além dos 200 relatos mais recentes. **Won’t neste piloto:** GPS curva a curva, cobertura municipal, certificação de calçadas, agendamento Atende+, van sob demanda, pagamentos e integração SPTrans sem acesso autorizado.

## Histórias e critérios de aceitação

### US01 — Consultar um percurso

Como usuário, quero selecionar origem e destino da rede para examinar o trajeto e seus limites.

1. Origem e destino vêm dos pontos cadastrados, sem exigir localização do usuário.
2. Dijkstra no Java respeita direção dos trechos, impedimentos e critérios; não renomeia uma rota comum de pedestres como acessível.
3. Dados desconhecidos, futuros ou com mais de 30 dias, degraus, piso incompatível, largura insuficiente e inclinação acima do limite excluem o trecho.
4. Resultado tem lista textual, distâncias, fonte, data e avisos; o esquema é complementar.
5. Ausência de resultado é informada como limite dos dados disponíveis, não como inexistência de alternativa real.
6. Origem igual ao destino e ponto inexistente têm comportamento explícito.
7. Barreiras bloqueantes alteram o cálculo e a comparação com a referência sem relatos.
8. Sintético não é apresentado como acessibilidade real comprovada.

**Evidências:** `RoutePlannerTest`, `PilotApiTest`, `RESULTADOS_E2E.json` e telas de planejamento/desvio.

### US02 — Registrar uma barreira

Como colaborador, quero registrar uma ocorrência num trecho para informar um impedimento.

1. Formulário exige trecho, categoria, descrição e indicação de bloqueio; o servidor registra data UTC.
2. Campos têm rótulos e limites; HTML faz validação básica e Java revalida o payload. Erros globais são anunciados; associação fina de todo erro de servidor ao campo é uma melhoria pendente.
3. Inserção retorna ID e persiste no H2 via JDBC, inclusive após reinício.
4. Novo relato é pendente, não verificado fisicamente.
5. Relato bloqueante pendente suspende conservadoramente o trecho.
6. API não expõe a conta autora; a interface orienta a não inserir dados pessoais.
7. Escrita exige sessão e CSRF. Não há papel de moderador atribuível pelo visitante.

**Evidências:** teste de navegador, teste de persistência e testes 401/403/400/404. O demonstrador não aceita dados reais como parte do piloto.

### US03 — Revisar situação

Como moderador, quero confirmar, rejeitar ou resolver um relato com justificativa e histórico.

1. Apenas perfil ADMIN pode alterar a situação; testes com perfil CITIZEN recebem 403.
2. Transação grava a situação e o histórico juntos, com data e autor interno não exposto.
3. Relato finalizado não é silenciosamente reaberto; ocorrência nova requer outro registro.
4. Resolver um relato não elimina outro bloqueio ativo, nem torna dados ausentes elegíveis.
5. Revisão persiste após reinício; nenhuma atualização é uma vistoria de campo.

**Evidências:** revisão no E2E, teste de múltiplos bloqueios, histórico, conflito 409 e comparação antes/depois do reinício.

### US04 — Transporte e serviços

Como usuário, quero conhecer conexões de transporte associadas aos pontos do piloto.

1. Listar exemplos com rótulo de simulação, ponto associado, fonte e data.
2. Separar informação do veículo das condições de embarque e do caminho até o ponto.
3. Sem token SPTrans, não exibir posição ou previsão como real.
4. Atende+ somente orienta aos canais oficiais e aos critérios de inscrição/programação.
5. A listagem é textual. Proximidade real, frota real e roteamento multimodal dependem de dados e integração futuros.

**Evidências:** tela de transporte e endpoint `/api/transport`. Não alegar van disponível, reserva ou parceria.

### US05 — Dados e indicadores

Como responsável, quero consultar totais, distribuições e efeitos de um bloqueio no grafo.

1. SQL agrega categoria, situação e relatos ativos por trecho.
2. Percentuais têm denominador explícito; sem registros, apresentar ausência de dados.
3. Desvio compara a rota atual com a mesma rede e os mesmos critérios sem relatos bloqueantes.
4. Distâncias e estatísticas são sintéticas, sem generalização para São Paulo.
5. Tempo de cálculo é medido no Java, não é tempo de viagem nem de tarefa humana.
6. Inserção e revisão atualizam os dados da interface.

**Evidências:** código SQL do repositório, testes do endpoint, base vazia e E2E. Não apresentar desempenho de uma execução como benchmark.

### US06 — Acessibilidade digital

Como usuário, quero realizar as tarefas sem depender do diagrama visual.

1. Usar rótulos, foco visível, texto e controles nativos de teclado.
2. Trajeto também é uma lista; cadastro usa seleção por formulário, sem arrastar mapa.
3. Cor não é a única indicação de bloqueio ou dado insuficiente.
4. Resultados, avisos e erros têm regiões de anúncio; revisar com leitor de tela real.
5. Registrar teclado, ampliação e leitor de tela, além da revisão automática.
6. Não declarar certificação WCAG com base em axe-core.

**Observado:** zero violações automáticas nos escopos examinados de quatro telas; link de salto e diálogo por Enter/Escape; layout de 390 px sem overflow da página. **Pendente:** avaliação com leitor de tela, ampliação, teclado nas tarefas completas e público-alvo.

## Método ágil aplicado

Quadro **A fazer → Em andamento → Validado**. “Validado” identifica apenas critérios verificados com evidência; não é homologação de ruas.

| Iteração | Trabalho e resultado observado | Situação |
| --- | --- | --- |
| 1 — Recorte e desenho | Referência Santo Amaro, modelo de dados, rede de teste e interface | Executada no recorte sintético; não houve levantamento territorial |
| 2 — Persistência | Inserção, consulta, revisão, sessão, CSRF e histórico em Java | Executada; persistência em H2 comprovada após reinício |
| 3 — Rotas e indicadores | Dijkstra, bloqueios, comparação de distâncias e SQL | Executada; integração testada no navegador |
| 4 — Avaliação e entrega | Revisão automática, teclado parcial, documentação e pacote | Parcial: avaliação humana, leitor de tela e os dois vídeos continuam pendentes |

Não houve estimativa de velocidade, horas ou capacidade inventada. As correções decorreram dos testes: ajuste do codificador BCrypt para login real, abertura de controles recolhidos no teste de navegador e alinhamento de texto no esquema.

## Próximas prioridades

1. Juliano experimentar o fluxo e avaliar a clareza dos rótulos e restrições.
2. Realizar avaliações voluntárias conforme `VALIDACAO_USUARIOS.md` e revisar acessibilidade digital.
3. Melhorar mensagens de campo, proteção contra abuso, paginação e cadastro seguro de dados verificados.
4. Gravar dois vídeos distintos, até três minutos cada; comercial sem implementação e técnico com Java/banco.
5. Acrescentar links reais num `.txt` e preparar o ZIP final para a FIAP. Ainda não há submissão nem URLs de vídeo.
