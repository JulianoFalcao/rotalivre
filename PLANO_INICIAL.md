# Rota Livre — Plano do piloto CitizenTech

**Responsável conhecido:** Juliano de Oliveira Falcão — RM 565907.  
**Data do planejamento:** 07/11/2026.  
**Entrega prevista no enunciado:** 19/11/2026, sem tolerância para atraso.  
**Situação:** ideia, piloto pequeno e Zona Sul de São Paulo aprovados; proposta inicial de recorte em Santo Amaro, com limites a detalhar; implementação, pesquisa com usuários e testes ainda não realizados. Nome Rota Livre provisório. Projeto independente do ArboriGov SP; não alterar seu código, banco ou domínio.

## 1. O problema e o benefício pretendido

Uma pessoa que utiliza cadeira de rodas precisa conhecer as condições do caminho antes de sair, e não apenas a distância até o destino. Degraus, obras, estreitamentos e ausência de rebaixamento podem interromper o deslocamento. O piloto pretende apoiar a escolha de percursos com informações rastreáveis sobre trechos e barreiras, além de indicar pontos e opções de transporte.

Essa hipótese precisa ser validada com as pessoas afetadas. A pesquisa inicial confirmou que há dados de ônibus e serviços acessíveis, mas não realizou entrevistas nem mediu dificuldades ou benefícios do público. O aplicativo não certificará acessibilidade nem garantirá segurança do deslocamento.

## 2. Recorte pequeno e demonstrável

A referência regional aprovada é a **Zona Sul de São Paulo**. Para manter o piloto pequeno, a proposta inicial é selecionar aproximadamente quatro a seis quarteirões no entorno do **Terminal Santo Amaro**, cujo endereço oficial é Avenida Padre José Maria, 400 [6]. O terminal será uma referência de transporte, não uma declaração de que suas entradas ou calçadas foram verificadas. Os limites e pontos exatos serão detalhados na modelagem; não haverá cobertura de toda a Zona Sul.

O objetivo é modelar aproximadamente doze pontos de decisão e vinte ligações, números de referência para controlar o esforço, não quantidade já levantada. Serviços do entorno só receberão nomes e posições reais depois de verificação; para testes, destinos como “serviço de saúde”, “comércio” e “ponto de ônibus” serão identificados como fictícios. Não cadastrar um estabelecimento real como acessível sem evidência.

Na primeira demonstração, a rede e as condições serão sintéticas, claramente identificadas como dados de teste. Quando houver dados reais, cada trecho terá origem, data e situação de verificação próprias. Um endereço real no mapa não transformará uma condição fictícia em uma observação verdadeira.

Origem e destino serão inicialmente pontos cadastrados do piloto. Não será um GPS completo para qualquer endereço da cidade. Localização atual poderá ser opcional e autorizada pelo usuário, sem guardar histórico; ausência de permissão não deverá impedir o uso por seleção manual.

## 3. Funcionalidades prioritárias e aceitação

### US01 — Consultar um percurso

Como usuário, quero escolher origem e destino para consultar os trechos considerados no trajeto e suas condições. O resultado precisa aparecer em lista textual, com distâncias e avisos, além de representação visual quando disponível. Cada trecho deve expor fonte, data e situação de verificação.

A implementação Java deverá calcular o caminho sobre um grafo: pontos são nós, ligações de calçada e travessias são arestas. A proposta é usar Dijkstra com custos não negativos e exclusão dos trechos incompatíveis com as restrições. Não será suficiente traçar uma rota comum de carro ou pedestre e chamá-la de acessível.

Condição desconhecida ou desatualizada não equivale a trecho adequado. No modo conservador, esses trechos ficam fora do cálculo. Se não houver caminho elegível, a resposta será “não encontramos percurso com os dados e critérios disponíveis”, sem concluir que não existe alternativa no mundo real. O rótulo de adequação dos dados sintéticos nunca se aplicará à rua real.

### US02 — Registrar uma barreira

Como colaborador, quero registrar uma barreira num trecho para alertar outras pessoas. A versão inicial coletará identificação do trecho, categoria, descrição curta e data. Fotografias podem ficar para uma iteração posterior, reduzindo exposição de pessoas e endereços e o esforço de armazenamento.

Inserção deverá persistir no banco, retornar identificador e aparecer na consulta após recarregar a página. Dados inválidos terão erro claro. Relato novo aparece como “não verificado”; quando indicar bloqueio, poderá suspender conservadoramente aquele trecho até revisão, sem afirmar uma vistoria oficial. As regras desse bloqueio devem ficar visíveis.

### US03 — Revisar e atualizar a situação

Como moderador autorizado, quero revisar um relato e registrar sua evolução. Confirmação, rejeição e resolução precisam de justificativa e data, preservando o histórico. Um relato resolvido só reabilita o trecho se não houver outro impedimento ativo ou dado de acessibilidade ausente.

O papel de moderador não poderá ser escolhido num campo pelo visitante. Consultas não exporão identidade do colaborador. Antes de disponibilizar operações de escrita publicamente, implementar autenticação e autorização no servidor. Uma demonstração restrita local não será descrita como portal produtivo seguro.

### US04 — Consultar transporte e serviços próximos

Como usuário, quero saber quais pontos e opções de transporte estão próximos ao percurso. A primeira versão exibirá pontos cadastrados e opções de demonstração, identificadas como simuladas. Deve distinguir caminho até o ponto, acessibilidade informada do veículo e condições de embarque desconhecidas.

A API Olho Vivo oferece posição e previsão com campo de acessibilidade do veículo; sua utilização depende de cadastro e token [1]. A integração real é uma prioridade condicionada à obtenção de acesso e ao teste de cobertura, não pré-requisito para concluir o núcleo do piloto. Dados indisponíveis ou antigos não serão apresentados como ônibus em tempo real. O campo da API não garante plataforma elevatória funcionando ou vaga disponível.

Para vans e transporte especial, o piloto dará informações e acesso aos canais oficiais do Atende+, sujeito a critérios, inscrição e programação próprios [2]. Não oferecer reservas, disponibilidade fictícia nem afirmar parceria. Planejamento multimodal completo e contratação de transporte permanecem no roadmap.

### US05 — Interpretar os dados do piloto

Como responsável pelo piloto, quero consultar totais por categoria, situação e trecho, e medir o efeito de uma barreira na rota calculada. As consultas SQL deverão produzir contagens e percentuais consistentes com a base. Comparação de distância original e alternativa poderá mostrar a diferença absoluta e percentual, deixando claro que o cálculo é sobre a rede de teste.

Quando não houver dados, o painel deve indicar isso, sem inventar resultados. Tempo computacional será medido por teste; não será confundido com tempo de viagem, economia de tempo para usuários ou benefício já comprovado. O relatório deve explicar que amostra pequena e sintética não permite generalizar estatísticas para São Paulo.

### US06 — Usar a interface sem depender do mapa

Como usuário, quero consultar e registrar informações com teclado e tecnologias assistivas. A interface terá rótulos explícitos, foco visível, erros associados aos campos e mensagens que não dependam só de cor. O trajeto terá alternativa textual; barreiras não poderão exigir exclusivamente clicar ou arrastar num mapa. Testar teclado, ampliação e ao menos um leitor de tela. As WCAG 2.2 serão referência, sem alegar conformidade completa antes da avaliação [3].

## 4. Stack e dados propostos

A proposta para o núcleo é **Java 21, Spring Boot, Maven, JDBC e H2 em arquivo**. Java atende diretamente ao requisito do vídeo técnico. Spring Boot organiza as rotas HTTP e a separação entre controller, serviço e repositório. JDBC facilita mostrar inserção, consulta e atualização no banco; usar parâmetros, transações e validação. H2 em arquivo permite execução local reproduzível e persistência após reiniciar, sem exigir conta externa. Esses componentes estão propostos, ainda não instalados e implementados como projeto.

A interface será web, em português, utilizável no computador e no celular. A escolha final de framework e ambiente de visualização dependerá da compatibilidade com o backend Java; uma interface que apenas salve no navegador não atenderá à proposta técnica. O backend Java deve ser a fonte das regras e do armazenamento demonstrados.

Entidades iniciais: ponto da rede, trecho com atributos e condição de verificação, relato de barreira, histórico de revisão, ponto de transporte e identidade mínima para as operações autorizadas. Não coletar diagnóstico, laudo médico, CPF ou histórico contínuo de localização para simples consulta de rotas. Preferências de percurso devem expressar restrições, não exigir informação médica.

Dados de demonstrador e dados observados permanecerão distinguíveis no schema e na interface. O cadastro de pontos e trechos terá controle do responsável pelo piloto, e a versão utilizada pelo algoritmo precisa ser rastreável.

## 5. Demonstração-chave e testes

O cenário do vídeo técnico será: consultar A → B; cadastrar uma obstrução num trecho do caminho; mostrar o registro persistido; recalcular e visualizar a alternativa. A atualização da situação e uma consulta de estatísticas completam a demonstração de banco. Caso a alternativa não exista, mostrar a resposta responsável em vez de inventar uma rota.

Os testes prioritários cobrem caminho elegível, bloqueio e desvio, origem igual ao destino, nó inexistente, falta de caminho, dados desconhecidos e obsoletos, entrada inválida, acesso indevido à moderação e persistência após reinício. Rever comportamento de trechos direcionais e múltiplas barreiras. Um grafo sintético permite afirmar resultados esperados de forma determinística, mas não substitui verificação em campo.

Para validação humana, convidar voluntariamente pessoas que utilizam cadeira de rodas e, se pertinente, acompanhantes. Pedir feedback sobre clareza dos avisos, utilidade dos atributos e execução das tarefas, preferencialmente numa sessão remota ou controlada. Não pedir que atravessem um percurso potencialmente perigoso para testar o aplicativo. Anonimizar os registros de avaliação e documentar número efetivo de participantes e limitações; não inventar respostas.

## 6. Contribuição aos ODS

A contribuição proposta ao **ODS 11, meta 11.2**, é apoiar informação sobre deslocamento e conexão com transporte, considerando explicitamente necessidades das pessoas com deficiência [4]. O piloto não executa obras, não certifica veículos nem mede o indicador oficial da ONU.

A relação com o **ODS 10, meta 10.2**, está no apoio à autonomia e à participação social por uma interface e informações mais inclusivas [5]. O impacto será uma hipótese até existir avaliação com o público; não basta citar os números dos ODS ou declarar uma redução de desigualdade medida.

## 7. Trabalho iterativo e entregas

Primeira iteração: confirmar área, problema e restrições; criar wireframes, diagrama de arquitetura, modelo do banco e backlog. Segunda: implementar persistência e cadastro/consulta/revisão. Terceira: algoritmo de rota e atualização por barreiras, seguido de indicadores e testes. Quarta: validação, correções de acessibilidade, documentação e gravação dos vídeos. Essas são etapas planejadas, não realizadas.

Manter um quadro simples “A fazer / Em andamento / Validado”, com critérios acima, revisão ao final de cada iteração e registro de mudanças. Priorizar um fluxo completo reproduzível antes de adicionar mapas sofisticados ou integrações externas.

O pacote de entrega terá código Java e instruções, documentação coerente com o que realmente funciona, diagramas, wireframes, backlog, testes e resultados observados. O vídeo comercial tem até três minutos e não mostra implementação; o vídeo técnico também tem até três minutos e mostra Java, banco e decisões técnicas. Links reais no YouTube e arquivo `.txt` serão acrescentados após gravação e publicação. Nenhum link de vídeo será inventado e nenhuma submissão FIAP será declarada sem execução autorizada.

Ficam fora do primeiro piloto: navegação GPS curva a curva, cobertura municipal completa, garantia de trajeto seguro, IA para certificar calçadas, chamada de van em tempo real, pagamento, atendimento emergencial e agendamento automático no Atende+.

## Próxima decisão

Detalhar o recorte proposto em Santo Amaro: selecionar pontos e ligações da rede de demonstração, desenhar as telas e modelar o banco. A aprovação da Zona Sul não implica que houve vistoria de calçadas ou aprovação de acessibilidade. Outro bairro da região pode substituir a referência sem mudar o núcleo técnico; dados reais dependem de verificação antes do uso operacional.

## Referências

[1]: https://www.sptrans.com.br/desenvolvedores/api-do-olho-vivo-guia-de-referencia/documentacao-api/ "SPTrans — documentação da API Olho Vivo"
[2]: https://www.sptrans.com.br/atende/requisitos "SPTrans — requisitos e inscrição no Atende+"
[3]: https://www.w3.org/TR/WCAG22/ "W3C — Web Content Accessibility Guidelines 2.2"
[4]: https://unhabitat.org/11-2-transport-system "UN-Habitat — meta 11.2, transporte acessível"
[5]: https://sdgs.un.org/goals/goal10 "ONU — ODS 10 e meta de inclusão 10.2"
[6]: https://www.sptrans.com.br/terminais/santo-amaro/ "SPTrans — localização do Terminal Santo Amaro"

Fontes consultadas em 07/11/2026. Este documento é um plano de escopo, não um relatório de funcionalidades ou validações concluídas.
