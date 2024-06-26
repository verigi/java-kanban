import task.managers.service_manager.InMemoryTaskManager;

public class InMemoryTaskManagerTest extends TaskManagersTest<InMemoryTaskManager> {

    @Override
    public InMemoryTaskManager init() {
        manager = new InMemoryTaskManager();
        return manager;
    }
}
