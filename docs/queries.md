# SQL-запити

У цьому документі описані SQL-запити, реалізовані у проєкті **Bank System**.  
Запити поділяються на **прості SELECT-запити** та **аналітичні запити**, які відповідають бізнес-питанням предметної області.

---

## 1. Прості SELECT-запити

Прості запити використовуються для отримання даних без складної аналітики.  
Вони застосовуються в сервісному шарі додатку для відображення інформації клієнтам.

---

### 1.1 Отримання всіх клієнтів

**Призначення:**  
Отримати список усіх активних клієнтів системи.

```sql
SELECT *
FROM customer
WHERE is_deleted = false;
```
### 1.2 Отримання рахунків конкретного клієнта

**Призначення:**  
Отримати всі рахунки, що належать певному клієнту.

```sql
SELECT *
FROM account
WHERE customer_id = :customerId
AND is_deleted = false;
```

---

```md
### 1.3 Отримання транзакцій за рахунком

**Призначення:**  
Отримати всі транзакції, повʼязані з конкретним рахунком (вхідні та вихідні).

```sql
SELECT *
FROM transactions
WHERE from_account_id = :accountId
   OR to_account_id = :accountId
ORDER BY date DESC;
```

---

### 1.4 Фільтрація транзакцій за типом

**Призначення:**  
Отримати транзакції певного типу (DEPOSIT, WITHDRAW, TRANSFER).

```sql
SELECT *
FROM transactions
WHERE type = :transactionType;
```

---

## 2. Аналітичні SQL-запити

Аналітичні запити використовуються для отримання агрегованих даних та аналізу активності клієнтів.

---

### 2.1 Аналітика транзакцій клієнтів

**Бізнес-питання:**  
Які клієнти мають найбільшу активність та обсяг транзакцій?

#### SQL-запит

```sql
SELECT
    c.customer_id AS customerId,
    c.name AS name,
    c.surname AS surname,
    COUNT(t.transaction_id) AS transactionCount,
    SUM(t.amount) AS totalAmount,
    AVG(t.amount) AS avgTransactionAmount,
    MAX(t.amount) AS maxTransactionAmount
FROM customer c
JOIN account a ON a.customer_id = c.customer_id
JOIN transactions t
    ON t.from_account_id = a.account_id
    OR t.to_account_id = a.account_id
GROUP BY c.customer_id, c.name, c.surname
ORDER BY totalAmount DESC;
```

