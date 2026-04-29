# Architecture Documentation

## Layer Dependencies
[Flow diagram showing Presentation → Domain ← Data]

## Dependency Injection
Manual DI via `AppContainer` class:
- Single instance of Room database
- Repository instances shared across ViewModels
- Use cases constructed with repository dependencies

## Navigation Flow
1. Login → Dashboard (mechanic/owner view)
2. Dashboard → Truck Detail → Tasks/NOTes
3. Dashboard → Check-in Form
4. Dashboard → Reports (owner only)
5. Settings → Profile → Password Change

## Data Flow
1. UI observes StateFlow from ViewModel
2. ViewModel calls Use Cases
3. Use Cases interact with Repositories
4. Repositories query Room DAOs
5. Room returns Flow<List<Entity>>
6. Entities mapped to Domain Models via extensions