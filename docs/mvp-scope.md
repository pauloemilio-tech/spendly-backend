# Spendly — Escopo do MVP

## 1. Objetivo do MVP

O Spendly é uma aplicação de gestão financeira pessoal que permite ao usuário cadastrar-se, autenticar-se, organizar carteiras, registrar receitas e despesas, acompanhar saldos e consultar um dashboard consolidado.

O objetivo do MVP é entregar uma experiência funcional, segura e demonstrável de controle financeiro pessoal, com isolamento de dados entre usuários, regras financeiras essenciais, persistência em PostgreSQL, testes automatizados e deploy acessível.

O MVP não pretende substituir uma solução bancária completa nem oferecer, nesta fase, recursos avançados de planejamento financeiro, integração bancária ou processamento de pagamentos.

## 2. Funcionalidades já implementadas

### Clientes e autenticação

- Cadastro de clientes.
- Validação de nome, CPF, senha e e-mail.
- Bloqueio de CPFs duplicados.
- Armazenamento da senha com BCrypt.
- Login com CPF e senha.
- Autenticação stateless com JWT.
- Expiração do token em 60 minutos.
- Consulta dos dados do usuário autenticado.
- Proteção dos endpoints privados com Spring Security.

### Carteiras

- Criação de carteira.
- Saldo inicial opcional.
- Validação para impedir saldo inicial negativo.
- Tipos de carteira:
  - BANK_ACCOUNT
  - CASH
  - CREDIT_CARD
  - INVESTMENT
  - DIGITAL_WALLET
- Listagem de carteiras ativas.
- Consulta individual de carteira.
- Atualização de nome e tipo.
- Soft delete por alteração do status para INACTIVE.
- Restrição de acesso ao proprietário da carteira.

### Transações

- Registro de receitas.
- Registro de despesas.
- Validação de valor maior que zero.
- Validação de compatibilidade entre tipo e categoria.
- Atualização automática do saldo da carteira.
- Bloqueio de despesas superiores ao saldo disponível.
- Atomicidade entre criação da transação e alteração do saldo.
- Listagem das transações do usuário.
- Consulta individual de transação.
- Restrição de acesso ao proprietário.

### Dashboard

- Saldo total das carteiras ativas.
- Total histórico de receitas.
- Total histórico de despesas.
- Quantidade de carteiras ativas.
- Quantidade total de transações.
- Cinco transações mais recentes.

### Segurança e isolamento

- Identificação do cliente pelo CPF autenticado no JWT.
- Consultas restritas pelo cliente proprietário.
- Proteção contra acesso a carteiras e transações de outros usuários por alteração de IDs.
- Configuração CORS externalizada.
- Aplicação sem sessão HTTP.
- CSRF desabilitado para a API stateless.

### Persistência, testes e execução

- Persistência com PostgreSQL.
- Execução local com Maven Wrapper.
- Execução com Docker e Docker Compose.
- Testes unitários de serviços.
- Testes de segurança e configuração.
- Testes de integração de repositórios com Testcontainers e PostgreSQL.
- Suíte atual com 22 testes, todos aprovados.
- Deploy de demonstração com frontend e backend acessíveis.

## 3. Funcionalidades obrigatórias para concluir o MVP

As seguintes funcionalidades ainda fazem parte do escopo final do MVP:

### Status e estorno de transações

- Integrar TransactionStatus à entidade Transaction.
- Definir status como ACTIVE e REVERSED, ou nomenclatura equivalente.
- Criar operação segura de estorno.
- Reverter corretamente o impacto da transação no saldo.
- Impedir estorno duplicado.
- Preservar o histórico da transação original.
- Criar testes para receitas e despesas estornadas.

### Filtros e paginação

- Adicionar paginação à listagem de transações.
- Adicionar ordenação por data.
- Permitir filtros básicos por:
  - Período.
  - Tipo.
  - Categoria.
  - Carteira.
- Garantir que todos os filtros continuem respeitando o usuário autenticado.

### Proteção contra concorrência

- Proteger atualizações de saldo contra concorrência.
- Utilizar locking pessimista ou controle otimista com @Version.
- Impedir atualizações perdidas em transações simultâneas.
- Criar testes para cenários concorrentes relevantes.

## 4. Requisitos técnicos obrigatórios

Para considerar o MVP tecnicamente concluído, também devem ser entregues:

### Banco de dados

- Adicionar Flyway.
- Criar migrations versionadas.
- Substituir ddl-auto=update por validate nos ambientes apropriados.
- Garantir que o banco possa ser criado do zero pelas migrations.
- Revisar índices, unicidade e restrições importantes.

### Perfis de configuração

- Separar configurações de desenvolvimento, teste, integração e produção.
- Manter credenciais e segredos fora do repositório.
- Documentar todas as variáveis de ambiente.

### Testes HTTP

- Criar testes de controllers e fluxos HTTP.
- Cobrir autenticação JWT.
- Cobrir validações.
- Cobrir respostas de erro.
- Cobrir isolamento entre usuários.
- Cobrir filtros, paginação e estorno.

### Documentação e observabilidade

- Adicionar OpenAPI e Swagger.
- Adicionar Spring Boot Actuator.
- Disponibilizar health check.
- Padronizar logs importantes sem expor dados sensíveis.

### Integração contínua

- Configurar GitHub Actions.
- Executar build e testes automaticamente.
- Garantir suporte ao Docker para Testcontainers.
- Manter a pipeline verde na branch principal.

### Integração e validação final

- Revisar a integração completa com o frontend.
- Validar estados de carregamento, erro e sessão expirada.
- Fazer smoke test no ambiente de deploy.
- Confirmar o fluxo completo de cadastro até dashboard.
- Revisar o README final.
- Preparar roteiro de demonstração.

## 5. Fora do escopo do MVP

Os seguintes recursos não são necessários para concluir esta versão:

- Transferências entre carteiras.
- Transações recorrentes.
- Agendamento de transações.
- Recuperação de senha.
- Alteração de senha.
- Exclusão definitiva de clientes.
- Reativação de carteiras.
- Integração com bancos.
- Open Finance.
- Processamento de pagamentos.
- Chaves de pagamento.
- Cartão de crédito com fatura e limite.
- Metas financeiras.
- Orçamentos mensais.
- Notificações.
- Relatórios avançados.
- Exportação de dados.
- Aplicativo mobile.
- Login social.
- Autenticação multifator.
- Auditoria completa de login.
- Sistema administrativo.
- Múltiplas moedas.

A existência de entidades ainda não integradas, como PaymentKey e LoginAudit, não obriga sua inclusão no MVP. Elas podem ser removidas temporariamente ou mantidas apenas como estruturas futuras, desde que isso fique documentado.

## 6. Roadmap pós-MVP

Após a conclusão do MVP, poderão ser consideradas:

- Transferências entre carteiras.
- Transações recorrentes.
- Planejamento mensal.
- Metas financeiras.
- Relatórios por categoria e período.
- Exportação em CSV ou PDF.
- Recuperação de senha.
- Reativação de carteiras.
- Auditoria de login.
- Integração bancária.
- Open Finance.
- Notificações.
- Aplicativo mobile.
- Melhorias de observabilidade e escalabilidade.

Esses itens devem ser avaliados somente após a conclusão, validação e demonstração do escopo atual.

## 7. Critérios de conclusão do MVP

O Spendly será considerado concluído como MVP quando:

- Todos os endpoints previstos no escopo estiverem funcionando.
- Cadastro e autenticação JWT estiverem estáveis.
- Carteiras puderem ser criadas, atualizadas, consultadas e desativadas.
- Receitas e despesas atualizarem o saldo corretamente.
- Transações puderem ser filtradas e paginadas.
- Transações puderem ser estornadas com segurança.
- Atualizações concorrentes não causarem inconsistência de saldo.
- O isolamento entre usuários estiver validado.
- O banco for controlado por migrations.
- Todos os testes automatizados estiverem aprovados.
- Os principais fluxos HTTP estiverem cobertos.
- Swagger e health check estiverem disponíveis.
- A pipeline de CI estiver aprovada.
- Frontend e backend estiverem integrados.
- O deploy estiver acessível e validado por smoke test.
- O README estiver atualizado.
- Uma pessoa conseguir testar e compreender o produto sem auxílio direto do desenvolvedor.

## 8. Sequência oficial de conclusão

A conclusão do MVP seguirá esta ordem:

1. Rodar e corrigir a suíte completa.
2. Criar o documento de escopo final.
3. Implementar status e estorno de transações.
4. Implementar filtros e paginação.
5. Proteger saldo contra concorrência.
6. Adicionar Flyway e perfis.
7. Criar testes HTTP.
8. Adicionar Swagger e Actuator.
9. Configurar GitHub Actions.
10. Integrar e revisar o frontend.
11. Fazer smoke test do deploy.
12. Finalizar README e demonstração.

## 9. Estado atual

- Passo 1 concluído.
- Suíte executada com Docker e Testcontainers.
- PostgreSQL 17 iniciado com sucesso nos testes de integração.
- 22 testes executados.
- 0 falhas.
- 0 erros.
- 0 testes ignorados.
- Build concluído com sucesso.
- Passo 2 concluído.
- Próximo passo: implementar status e estorno de transações
