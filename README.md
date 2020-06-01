
CREATE ACCOUNT

```
 curl -i -X POST -H "Content-Type: application/json" -d '{"userId":"123-123123"}' http://localhost:18080/accounts
```

FETCH ACCOUNT

```
 curl -i -X GET -H "Content-Type: application/json" http://localhost:18080/accounts/85                          22:39:05
```

DEPOSIT AMOUNT

```
curl -i -X PATCH -H "Content-Type: application/json" http://localhost:18080/accounts/85/balance/10
```

WITHDRAW AMOUNT

```
curl -i -X DELETE -H "Content-Type: application/json" http://localhost:18080/accounts/85/balance/10
```

FUNDS TRANSFER

```
curl -i -X POST -H "Content-Type: application/json" -d '{"userId":"123-123123"}' http://localhost:18080/accounts
curl -i -X POST -H "Content-Type: application/json" -d '{"userId":"2344"}' http://localhost:18080/accounts
curl -i -X PATCH -H "Content-Type: application/json" http://localhost:18080/accounts/64/balance/10
curl -i -X POST -H "Content-Type: application/json" -d '{"from":"64", "to": "65", "amount": 10}' http://localhost:18080/transfers
curl -i -X GET -H "Content-Type: application/json" http://localhost:18080/accounts/64
curl -i -X GET -H "Content-Type: application/json" http://localhost:18080/accounts/65
❯ curl -i -X GET -H "Content-Type: application/json" http://localhost:18080/transfers/73
```
