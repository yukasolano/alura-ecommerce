# Alura - Curso Kafka

## Inicialização

Precisa inicializar o zookeeper

```
bin/zookeeper-server-start.sh config/zookeeper.properties
```

E o kafka

```
bin/kafka-server-start.sh config/server.properties
bin/kafka-server-start.sh config/server2.properties
```

Daí pode executar consumers e producers

## Execução

### Para criar uma ordem:
http://localhost:8080/new?email=nome@email.com&amount=5000&uuid=123456

| | | |
|---|---|---|
| NewOrderServlet: ECOMMERCE_NEW_ORDER  | CreateUserService                                                      | |
|                                       | FraudDetectorService: ECOMMERCE_ORDER_FRAUD ou ECOMMERCE_ORDER_APPROVED | |
|                                       | EmailNewOrderService: ECOMMERCE_SEND_EMAIL  | EmailService |


### Para gerar relatórios:
http://localhost:8080/admin/generate-reports

| | | |
|---|---|---|
| GenerateAllReportsServlet: ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS  | BatchSendMessageService: For each user ECOMMERCE_USER_GENERATE_READING_REPORT | ECOMMERCE_USER_GENERATE_READING_REPORT |


### Log

Ouve todos os tópicos que começam com ECOMMERCE_*