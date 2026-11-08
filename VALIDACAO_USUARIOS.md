# Rota Livre — Plano de validação com usuários

**Autor:** Juliano Falcão (Juliano de Oliveira Falcão) — RM 565907.  
**Referência de planejamento:** 07/11/2026.  
**Estado:** sessões, entrevistas e verificações planejadas; execução e resultados a verificar pelo responsável principal.

Este documento é um roteiro, não um relatório de pesquisa concluída. Existe um protótipo técnico 0.1.0; suas verificações estão em `EVIDENCIAS.md`. Não houve avaliação humana, e não há participantes, tempos, respostas ou taxas de sucesso presumidos. Os modelos de registro estão em branco e só devem ser preenchidos com observações reais.

## 1. Objetivo e contexto do piloto

Avaliar se pessoas que utilizam cadeira de rodas e, quando pertinente, acompanhantes conseguem consultar uma rota, registrar uma barreira e interpretar os limites das informações. A sessão busca dificuldades de uso, clareza e utilidade percebida; não comprova segurança do deslocamento, impacto social ou acessibilidade de ruas.

O Rota Livre é CitizenTech independente do ArboriGov. O recorte previsto abrange quatro a seis quarteirões no entorno do Terminal Santo Amaro, Zona Sul de São Paulo. A demonstração utiliza uma rede prevista de **12 nós e 20 ligações sintéticas**, não um levantamento de campo. Nenhum percurso é certificado. Dados desconhecidos, desatualizados ou incompatíveis são excluídos do cálculo conservador.

A versão de referência é web: HTML, CSS e JavaScript servidos por Java 21/Spring Boot, com JDBC e H2 persistente em arquivo. A versão e as funcionalidades realmente disponíveis deverão ser verificadas antes de cada sessão. Transporte listado é demonstração, sem token SPTrans ou promessa de tempo real; Atende+ oferece somente orientação aos canais oficiais.

## 2. Participação voluntária e proteção dos participantes

Convidar voluntariamente pessoas que utilizam cadeira de rodas e, se pertinente, acompanhantes. Não existe quantidade de participantes confirmada. Registrar somente o número efetivamente participante e as limitações do recrutamento. Se não houver participação, documentar a validação humana como pendente; não substituir por depoimentos fictícios.

Realizar sessão **remota**, utilizando apenas o demonstrador. Não solicitar caminhada, travessia, visita ao terminal, teste de calçada ou deslocamento em percurso potencialmente perigoso. A pessoa pode pausar, pular tarefa ou encerrar sem justificativa; interrupções não devem ser interpretadas automaticamente como falha do produto.

Cuidados previstos:

- Explicar finalidade acadêmica, uso dos registros e limites antes de começar; confirmar consentimento para participação e anotações. Sem consentimento, não realizar nem registrar a sessão.
- Usar um código sem vínculo nominal no registro. Não guardar lista que associe código a nome, contato ou conta de reunião. Organizar convite e comunicação separadamente das observações e sem copiar contatos para o relatório.
- Não coletar diagnóstico, laudo, CPF, endereço residencial, percurso habitual, localização ou histórico de deslocamento. Não perguntar causa da deficiência nem informações médicas.
- Perguntar apenas quais ajustes de interação são desejados para a sessão, sem exigir justificativa. Tecnologia assistiva ou dispositivo podem ser registrados apenas se pertinentes e consentidos, sem dados identificadores.
- Não gravar áudio, vídeo ou tela por padrão. Se isso for necessário em outro protocolo, exigir consentimento específico e definir acesso, retenção e exclusão antes de coletar; este roteiro usa anotações mínimas.
- Evitar dados pessoais em relatos e compartilhamento de tela. Usar descrições fictícias; orientar a ocultar notificações e a não mostrar contas ou arquivos pessoais.
- Armazenar registros com acesso restrito ao responsável. Antes da coleta, definir local, prazo de retenção e forma de exclusão; não deixar essa decisão para depois.
- Registrar feedback sem nomes ou detalhes reconhecíveis. Anonimizar também citações e contexto; preferir paráfrase fiel quando uma fala permitir identificação.

Não prometer anonimato técnico absoluto da plataforma de chamada: nome de exibição ou conta podem aparecer durante a sessão. Explicar essa limitação e manter os **registros da avaliação anônimos**, sem copiar esses identificadores.

### Texto de abertura sugerido

> Estamos avaliando um demonstrador acadêmico do Rota Livre. Queremos observar o uso da interface e entender suas impressões; não estamos avaliando você. Os percursos e as condições apresentados são sintéticos, não certificados e não devem orientar um deslocamento real. A participação é voluntária: você pode parar, pular qualquer tarefa ou pedir uma pausa. Não pediremos diagnóstico, CPF ou localização. Pretendemos fazer apenas anotações sem seu nome, sem gravar a sessão. Podemos iniciar e registrar essas observações?

## 3. Preparação e condução

Antes da sessão, o facilitador deverá verificar uma versão identificável, preparar um ambiente de dados de teste separado de qualquer uso operacional e confirmar que as tarefas propostas podem ser tentadas. Preparar origem e destino fictícios disponíveis, um trecho de teste e mensagens de demonstração, desconhecimento, bloqueio e ausência de caminho quando disponíveis. Não afirmar que esses cenários já foram implementados.

Registrar o estado inicial da base e o que será restaurado entre sessões para evitar que uma barreira cadastrada por um participante altere silenciosamente a tarefa do seguinte. Se um cenário necessário não estiver disponível, registrar a limitação e não simular um resultado como se fosse funcional.

Na sessão:

1. Ler a abertura, esclarecer dúvidas e confirmar consentimento.
2. Verificar ajustes de interação desejados, condições de conexão e possibilidade de usar o próprio teclado ou tecnologia assistiva. Nenhum ajuste exige informação médica.
3. Apresentar as três tarefas abaixo sem ensinar previamente o caminho esperado na interface.
4. Observar e anotar ações, mensagens, erros, dúvidas e intervenções. Falar pouco; evitar conduzir cliques ou sugerir que a aplicação é fácil ou segura.
5. Se houver pedido de ajuda, registrar o momento e a orientação dada. Ajuda do facilitador não pode ser omitida ao classificar o resultado.
6. Encerrar com perguntas neutras, agradecer e reafirmar que não há recomendação de deslocamento real.

Pedir que a pessoa comente o que pensa é opcional. Não exigir fala contínua, sobretudo quando interfere no uso de tecnologia assistiva. Não estabelecer duração fictícia nem impor rapidez.

## 4. Três tarefas propostas

### T1 — Consultar uma rota

**Enunciado neutro:** “Neste demonstrador, escolha a origem e o destino de teste indicados para a sessão e procure as informações do percurso. Mostre o que você usaria para entender os trechos apresentados.”

**Observar:** localização dos controles, seleção de pontos, uso da consulta, leitura da alternativa textual, entendimento de distâncias e descoberta de fonte, data e situação dos dados. Não exigir uso de mapa.

**Conclusão esperada para o cenário preparado:** encontrar e explicar o resultado textual ou, se o cenário não tiver caminho elegível, reconhecer a mensagem correspondente sem concluir que não existe alternativa na cidade. O resultado esperado do cenário deve ser anotado pelo facilitador antes da tentativa.

**Perguntas após a tentativa:**

- “O que você entendeu deste resultado?”
- “Que informações você procurou e quais encontrou?”
- “Houve algo difícil de localizar ou entender?”

### T2 — Registrar uma barreira

**Enunciado neutro:** “Imagine que no trecho de teste indicado há uma obstrução fictícia. Registre essa informação usando os dados de teste fornecidos e procure saber o que aconteceu com o registro.”

**Observar:** seleção do trecho sem mapa obrigatório, compreensão de categoria/descrição/data, preenchimento, percepção de erros, envio e reconhecimento da confirmação. Usar apenas conteúdo fictício sem identificação pessoal.

**Conclusão esperada:** localizar o registro persistido na consulta após recarregar a página, identificar que o relato começa não verificado e descrever o retorno apresentado. A inspeção do banco e a persistência após reinício são verificações técnicas separadas, não uma responsabilidade do participante.

**Perguntas após a tentativa:**

- “Como você soube se o registro foi recebido?”
- “O que o estado exibido significa para você?”
- “O que você esperaria que acontecesse depois do envio?”

### T3 — Interpretar os avisos

**Enunciado neutro:** “Leia os avisos disponíveis sobre os dados e o transporte neste demonstrador. Explique com suas palavras o que eles permitem ou não permitem concluir antes de decidir um deslocamento.”

**Observar:** entendimento espontâneo dos rótulos, procura por detalhes e possíveis interpretações de garantia. Apresentar apenas mensagens reais da versão preparada; se faltarem, registrar a pendência.

**Pontos de entendimento a verificar, sem antecipá-los na pergunta inicial:**

- Rede e condições sintéticas não descrevem uma vistoria da rua; nenhum percurso é certificado.
- Dados desconhecidos ficam fora do cálculo conservador, não são considerados adequados.
- “Não encontramos percurso com os dados e critérios disponíveis” não significa que não existe alternativa no mundo real.
- Relato não verificado não equivale a confirmação oficial.
- Transporte da demonstração não é previsão ou disponibilidade em tempo real. Informações sobre veículo não garantem condições de embarque ou funcionamento de equipamento.
- Atende+ é orientação para canais oficiais e regras próprias, não reserva ou elegibilidade garantida.

**Perguntas de aprofundamento neutras:**

- “O que a expressão ‘dados de demonstração’ significa nesta tela?”
- “O que você entende por ‘condição desconhecida’?”
- “O que esta mensagem de ausência de percurso informa, na sua opinião?”
- “O que você entende que a opção de transporte oferece?”

A explicação correta feita pelo facilitador ao final pode apoiar segurança e esclarecimento, mas não conta como compreensão espontânea. Registrar respostas anteriores e posteriores à ajuda separadamente.

## 5. Perguntas finais neutras

1. “Que parte, se alguma, foi útil para você? Por quê?”
2. “O que faltou para compreender melhor as informações?”
3. “Em algum momento a interface sugeriu algo diferente do que você esperava?”
4. “Como você mudaria a forma de apresentar os avisos?”
5. “Que dificuldade de interação você encontrou, se encontrou alguma?”
6. “Há algo que não perguntamos e que você gostaria de comentar?”

Evitar perguntas indutoras, como “Você achou fácil?”, “Essa rota é segura, certo?” ou “O aplicativo aumentaria sua autonomia?”. Utilidade percebida é feedback, não impacto comprovado. Não pedir diagnóstico ou justificativa clínica para preferências.

## 6. Métricas e interpretação

### Sucesso da tarefa

Classificar cada tentativa com base no objetivo do cenário e na observação:

| Classificação | Regra de registro |
| --- | --- |
| Concluída sem ajuda do facilitador | Objetivo alcançado e, quando pertinente, corretamente explicado, sem orientação do facilitador. Uso da tecnologia assistiva habitual não é ajuda do facilitador. |
| Concluída com ajuda do facilitador | Objetivo alcançado após intervenção; registrar o conteúdo da ajuda. |
| Não concluída | Houve tentativa, mas o objetivo não foi alcançado; descrever a barreira observada sem atribuir incapacidade ao participante. |
| Interrompida | A pessoa encerrou ou pediu pausa definitiva; registrar apenas a razão voluntariamente oferecida, se pertinente e não identificável. |
| Não realizada | Sem tentativa, por exemplo cenário indisponível ou decisão de não executar. Não registrar como sucesso ou fracasso. |

Se houver dados, apresentar contagem por classificação e por tarefa. Uma taxa de sucesso sem ajuda, caso utilizada, será: **tentativas concluídas sem ajuda ÷ tentativas avaliáveis**, com numerador e denominador explícitos. Tentativas avaliáveis incluem concluídas com ou sem ajuda e não concluídas. Informar separadamente interrupções e tarefas não realizadas; não ocultá-las. Não generalizar uma amostra pequena ou voluntária para toda a população.

### Tempo observado

Medir somente durante a sessão real, após a pessoa indicar que compreendeu o enunciado e antes de iniciar a primeira ação, até conclusão ou encerramento da tentativa. Registrar início/fim ou duração observada, unidade, pausas e interrupções de conexão. Diferenciar tempo total de tempo ativo caso sejam ambos registrados; não descontar pausas sem documentar o método.

Se não houver medição confiável, preencher “não medido”, em vez de estimar. Nenhum número de segundos ou minutos é antecipado neste documento. Tempo observado de tarefa não é tempo de viagem, tempo computacional do algoritmo ou economia de tempo produzida pela solução. Não comparar tempos de ambientes e ajustes diferentes sem explicitar essas diferenças.

### Feedback qualitativo

Registrar dificuldades, dúvidas, expectativas, aspectos úteis e sugestões com linguagem fiel e anônima. Separar observação do facilitador, relato da pessoa e interpretação posterior. Em T3, anotar quais limites foram compreendidos espontaneamente, após pergunta ou somente depois de explicação. Uma interpretação de garantia indevida deve gerar revisão prioritária de comunicação, mesmo que a tarefa tenha sido concluída.

Não inventar respostas, médias, taxas, benefícios ou número de participantes. Agregações só serão produzidas após registros reais e conferência dos denominadores.

## 7. O que cada tipo de avaliação demonstra

| Tipo | Verificações planejadas | Limites |
| --- | --- | --- |
| Testes automatizados e técnicos | Algoritmo no grafo sintético; exclusão de dados desconhecidos/obsoletos; bloqueio/desvio; origem igual ao destino; nó inválido; falta de caminho; múltiplas barreiras; entradas inválidas; autorização; consultas SQL e persistência após reinício. | Evidenciam comportamento nos cenários testados, não compreensão humana, condições da rua ou conformidade integral de acessibilidade. |
| Avaliação de acessibilidade da interface | Teclado, foco, ordem de navegação, ampliação, rótulos, erros, mensagens e pelo menos um leitor de tela; ferramentas automáticas apenas como apoio. | WCAG 2.2 é referência. Ferramenta sem alertas, um leitor de tela ou uma sessão bem-sucedida não certifica WCAG nem cobre todas as pessoas e tecnologias. |
| Avaliação de usabilidade com voluntários | As três tarefas, sucesso observado, tempo realmente medido, compreensão dos avisos e feedback qualitativo. | Depende da versão, contexto e participantes efetivos; não valida algoritmo por si só, não substitui avaliação de acessibilidade e não certifica percurso. |

Documentar ambiente, versão, método e resultados separadamente. Uma verificação aprovada não autoriza marcar todas as outras como concluídas. Este roteiro não inclui vistoria física nem avaliação de segurança viária.

## 8. Modelos de registro em branco

### 8.1 Preparação do protocolo — preencher antes de coletar

| Campo | Preenchimento |
| --- | --- |
| Responsável pela coleta |  |
| Versão da aplicação e estado da base de teste |  |
| Meio remoto e limitações de privacidade informadas |  |
| Local restrito de armazenamento |  |
| Prazo e procedimento de exclusão dos registros |  |
| Ajustes de interação disponíveis |  |
| Cenário de T1 e resultado esperado |  |
| Trecho e dados fictícios de T2 |  |
| Avisos reais disponíveis para T3 |  |
| Procedimento de restauração da base entre sessões |  |
| Funcionalidades ausentes / limites da sessão |  |

### 8.2 Registro anônimo de sessão

| Campo | Preenchimento |
| --- | --- |
| Código sem vínculo nominal |  |
| Data da sessão |  |
| Consentimento para participação e anotações confirmado |  |
| Versão e cenário usados |  |
| Ambiente/dispositivo pertinente, sem identificação pessoal |  |
| Ajustes ou tecnologia assistiva, apenas se consentidos |  |
| Interrupções ou limitações |  |

Não incluir nome, contato, conta de reunião, diagnóstico, CPF ou endereço.

### 8.3 Observação das três tarefas

| Tarefa | Objetivo do cenário | Classificação | Início/fim ou duração observada e unidade | Pausas / medição não disponível | Ajuda do facilitador | Evidência e dificuldade observadas |
| --- | --- | --- | --- | --- | --- | --- |
| T1 — Consultar rota |  |  |  |  |  |  |
| T2 — Registrar barreira |  |  |  |  |  |  |
| T3 — Interpretar avisos |  |  |  |  |  |  |

### 8.4 Compreensão e feedback

| Aviso / tema | Resposta anônima ou paráfrase fiel | Espontânea / após pergunta / após ajuda | Observação distinta da interpretação | Mudança sugerida |
| --- | --- | --- | --- | --- |
| Dados sintéticos / percurso não certificado |  |  |  |  |
| Condição desconhecida / ausência de caminho |  |  |  |  |
| Relato não verificado |  |  |  |  |
| Transporte demonstrativo / condições de embarque |  |  |  |  |
| Atende+ orientativo |  |  |  |  |
| Utilidade, dificuldades e outros comentários |  |  |  |  |

### 8.5 Consolidação — preencher somente após sessões reais

| Campo | Resultado observado |
| --- | --- |
| Número efetivo de participantes |  |
| Tentativas e resultados por tarefa, com denominadores |  |
| Tempos confiavelmente medidos e método |  |
| Padrões qualitativos, sem identificar pessoas |  |
| Limitações de amostra, ambiente e versão |  |
| Avaliações técnicas e de acessibilidade realizadas / pendentes |  |
| Problemas prioritários e histórias do backlog relacionadas |  |
| Correções propostas, responsável e estado a verificar |  |
| Evidência de reavaliação, se efetivamente realizada |  |

## 9. Uso dos resultados no backlog

Após uma sessão real, relacionar problemas a US01–US06, priorizar interpretação indevida de garantia, impossibilidade de tarefa e barreiras de interação, e registrar mudanças com a versão. O responsável principal atualizará o quadro **A fazer / Em andamento / Validado** apenas com evidência pertinente. Se uma correção não tiver sido reavaliada, registrar essa pendência.

A apresentação acadêmica deve informar o que foi efetivamente testado, por qual método e com quais limites. Nenhum roteiro preenchido parcialmente comprova entrevista completa, eficácia do aplicativo, segurança de percurso ou conformidade WCAG.

## 10. Fontes já fornecidas

- [Plano inicial do piloto Rota Livre](PLANO_INICIAL.md), especialmente funcionalidades, salvaguardas, testes e validação humana. A implementação atual está em `ESPECIFICACAO_TECNICA.md`.
- [Enunciado CitizenTech](ENUNCIADO.txt), especialmente escuta das pessoas, resultados medidos, inclusão, segurança e proteção de dados.

Não houve nova pesquisa nem realização de sessões neste documento. As referências externas já estão no plano; o roteiro utiliza essas fontes e as restrições do piloto.
