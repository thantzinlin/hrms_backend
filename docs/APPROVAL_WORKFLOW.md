# Multi-Level Approval Workflow

## API Base

Assume base URL: `http://localhost:8080/api` and `Authorization: Bearer <token>` where needed.

---

## Sample JSON Requests

### 1. POST /leave-requests

**Request**
```json
{
  "employeeId": "EMP00001",
  "startDate": "2025-03-01",
  "endDate": "2025-03-03",
  "reason": "Family trip",
  "leaveType": "ANNUAL"
}
```

**Response (201)**
```json
{
  "returnCode": "200",
  "returnMessage": "Success",
  "data": {
    "id": 1,
    "employeeId": 1,
    "employeeName": "John Doe",
    "startDate": "2025-03-01",
    "endDate": "2025-03-03",
    "reason": "Family trip",
    "status": "PENDING_SUPERVISOR",
    "leaveType": "ANNUAL"
  }
}
```

---

### 2. POST /overtime-requests

**Request**
```json
{
  "employeeId": "EMP00001",
  "date": "2025-02-15",
  "hours": 3.5,
  "reason": "Project deadline"
}
```

**Response (201)**
```json
{
  "returnCode": "200",
  "returnMessage": "Success",
  "data": {
    "id": 1,
    "employeeId": "EMP00001",
    "employeeName": "John Doe",
    "date": "2025-02-15",
    "hours": 3.5,
    "reason": "Project deadline",
    "status": "PENDING_SUPERVISOR"
  }
}
```

---

### 3. GET /approvals/pending

**Response (200)**
```json
{
  "returnCode": "200",
  "returnMessage": "Success",
  "data": [
    {
      "id": "LEAVE-1",
      "requestType": "LEAVE",
      "requestId": 1,
      "requesterEmployeeId": "EMP00001",
      "requesterName": "John Doe",
      "startDate": "2025-03-01",
      "endDate": "2025-03-03",
      "reason": "Family trip",
      "leaveType": "ANNUAL",
      "hours": null
    },
    {
      "id": "OVERTIME-2",
      "requestType": "OVERTIME",
      "requestId": 2,
      "requesterEmployeeId": "EMP00001",
      "requesterName": "John Doe",
      "startDate": "2025-02-15",
      "endDate": null,
      "reason": "Project deadline",
      "leaveType": null,
      "hours": 3.5
    }
  ]
}
```

---

### 4. POST /approvals/{id}/approve

**Path:** `POST /approvals/LEAVE-1/approve`

**Body (optional remarks)**
```json
{
  "remarks": "Approved. Please hand over tasks before leave."
}
```

**Response (200)**
```json
{
  "returnCode": "200",
  "returnMessage": "Approved successfully",
  "data": null
}
```

**Alternative: body with requestType and requestId**
```json
{
  "requestType": "LEAVE",
  "requestId": 1,
  "remarks": "Approved."
}
```
Use `POST /approvals/approve` for this format.

---

### 5. POST /approvals/{id}/reject

**Path:** `POST /approvals/LEAVE-1/reject`

**Body (optional)**
```json
{
  "remarks": "Peak period; please reschedule."
}
```

**Response (200)**
```json
{
  "returnCode": "200",
  "returnMessage": "Rejected",
  "data": null
}
```

---

### 6. GET /org/hierarchy

**Response (200)**
```json
{
  "returnCode": "200",
  "returnMessage": "Success",
  "data": [
    {
      "id": 1,
      "employeeId": "EMP00001",
      "name": "CEO Name",
      "email": "ceo@company.com",
      "reportingToId": null,
      "subordinates": [
        {
          "id": 2,
          "employeeId": "EMP00002",
          "name": "Manager Name",
          "email": "manager@company.com",
          "reportingToId": 1,
          "subordinates": [
            {
              "id": 3,
              "employeeId": "EMP00003",
              "name": "Employee Name",
              "email": "emp@company.com",
              "reportingToId": 2,
              "subordinates": []
            }
          ]
        }
      ]
    }
  ]
}
```

---

## Leave types (leaveType)

- `ANNUAL`
- `SICK`
- `CASUAL`
- `MATERNITY`
- `PATERNITY`
- `UNPAID`

---

## Approval flow

1. Employee submits → status `PENDING_SUPERVISOR`.
2. Supervisor (resolved from hierarchy + `approval_authorities`) approves → status `PENDING_HR`.
3. HR (from `approval_authorities` where `is_hr = 1`) approves → status `APPROVED`.
4. Reject at any step → status `REJECTED`; workflow stops.

Approvers are resolved dynamically; no approver is stored on `leave_requests` or `overtime_requests`. History is stored in `approval_history`.

---

## Why GET /approvals/pending might return empty (supervisor)

For a supervisor to see pending leave/overtime in **GET /approvals/pending**, both of these must be true:

1. **Reporting chain:** The employee who submitted the request must have **Reports to** set to that supervisor (Employee form → "Reports to" = the supervisor).
2. **Approval authority:** The supervisor’s employee record must have **Can approve leave** (and/or **Can approve overtime**) checked (Employee form → "Approval authority" → "Can approve leave" / "Can approve overtime").

If either is missing, the supervisor will not be the “resolved” approver and the request will not appear in their pending list. Edit the **supervisor’s** employee record and ensure "Can approve leave" (and "Can approve overtime" if needed) is checked.
