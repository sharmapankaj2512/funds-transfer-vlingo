
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