# Security Policy

This project handles garbage-and-recycling collection-route operating
workflows. Treat vulnerabilities as potentially high impact even when
the demo data is synthetic — this domain's failure modes include
physical worker-safety risk (vehicle-traffic hazard: moving collection
vehicle/traffic injury) and hazardous-material-handling risk (sharp/
hazardous waste-item injury, equipment condition).

## Do Not Disclose Publicly

Report privately before opening public issues for:

- credential exposure
- real worker, route or operator data exposure
- authorization bypass
- WasteCollectionGovernor bypass
- audit-ledger tampering
- over-disclosure in reports or exports
- unsafe robot action dispatch
- any path that lets a proposal reach a collection-execution decision,
  a route-safety-clearance decision (e.g. declaring a route cleared
  for safety), or a route-safety-supervisor override decision

## Reporting

Use GitHub private vulnerability reporting when available for the repository.
If that is unavailable, contact the repository maintainers through the
cloud-itonami organization before publishing details.

Include:

- affected commit or version
- reproduction steps
- expected and actual behavior
- impact on worker/route data, policy enforcement or audit logging
- suggested fix, if known

## Production Guidance

- Store secrets outside Git.
- Keep real worker/route/operator data outside this repository.
- Run policy tests before deployment.
- Export and review audit logs regularly.
- Use least privilege for operators and service accounts.
