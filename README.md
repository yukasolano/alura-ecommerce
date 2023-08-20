# Alura - Curso Kafka

## Preparação
Baixar o Kafka
https://kafka.apache.org/downloads

Descompactar:
cd dev
tar zxf ../Downloads/kafka_2.13-2.8.0.tgz

Configurar arquivos server.properties
Alterar:
broker.id=1
listeners=PLAINTEXT://:9091
log.dirs=logs/kafka-logs1
num.partitions=3
offsets.topic.replication.factor=3
transaction.state.log.replication.factor=3

Criar:
default.replication.factor=3

Criar outras copias do aruqivo server.properties e atualziar apenas o id, listener e o logdir 


## Inicialização

Precisa inicializar o zookeeper

```
bin/zookeeper-server-start.sh config/zookeeper.properties
```

E o kafka

```
bin/kafka-server-start.sh config/server1.properties
bin/kafka-server-start.sh config/server2.properties
bin/kafka-server-start.sh config/server3.properties
```

Daí pode executar consumers e producers
Alterar configuracao de execucao pqra MODULO_DIR

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


Para pegar info dos consumers:
bin/kafka-consumer-groups.sh --all-groups --bootstrap-server localhost:9091 --describe

Para pegar info dos topics:
bin/kafka-topics.sh --describe --bootstrap-server localhost:9091



Kafka cluster: tem varios brokers
Brokers: kafka server tem seus proprios local storage, manage partitions, each sg in Log dentified by offset
Zookeper: cluster management, failure detection & recovery, store ACLs & secrets
Topic: streams of related messages in kafka. Is a logical representation, categorizes messsges into groups
Partitions: para paralelizar o consumo de msg. Dentro de cada particiçao kafka garante consumo sequencial
Replicas: As partições leaders são as “mandachuvas”: é delas que consumimos e produzimos as mensagens,
enquanto as partições in sync são réplicas, atualizadas a cada ação da leader.
Lembrando que só conseguimos usar réplicas se temos um cluster com 2 ou mais brokers e o número máximo 
de réplicas é a mesma quantidade de brokers que temos no cluster.
Record: mensagem do kafka: header , key, value, timestamp