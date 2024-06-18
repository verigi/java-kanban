import org.junit.jupiter.api.*;

import task.managers.file_manager.FileBackedTaskManager;

import java.nio.file.Path;

public class FileBackedTaskManagerTest extends TaskManagersTest<FileBackedTaskManager> {

    Path path = Path.of("test/FileBackedTest.csv");

    @Override
    public FileBackedTaskManager init() {
        manager = new FileBackedTaskManager(path.toFile());
        return manager;
    }

    @Test
    @DisplayName("Проверка загрузки из файла")
    public void loadFromFileTest() {
        FileBackedTaskManager testManager = FileBackedTaskManager.load(path.toFile());
        Assertions.assertEquals(2, testManager.getAllTasks().size());
        Assertions.assertEquals(1, testManager.getAllEpicTasks().size());
        Assertions.assertEquals(2, testManager.getAllSubtasks().size());
    }

    @Test
    @DisplayName("Проверка загрузки из файла с историей")
    public void loadFromFileWithHistoryTest() {
        FileBackedTaskManager testManager = FileBackedTaskManager.load(path.toFile());

        Assertions.assertEquals(1,testManager.getHistory().size());
        Assertions.assertEquals(2,testManager.getAllTasks().size());
        Assertions.assertEquals(1,testManager.getAllEpicTasks().size());
        Assertions.assertEquals(2,testManager.getAllSubtasks().size());
    }
}
