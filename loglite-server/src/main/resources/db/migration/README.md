# Database migrations

Managed by Flyway, applied automatically on application startup.

## Rollback

Flyway Community does not run automatic "down" migrations. To roll back a
migration, apply the inverse DDL manually, e.g. for `V1__create_log_entries_table.sql`:

```sql
DROP TABLE IF EXISTS log_entries;
```
