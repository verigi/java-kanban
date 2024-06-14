import org.junit.jupiter.api.*;

import task.managers.file_manager.FileBackedTaskManager;

import java.nio.file.Path;
import java.util.List;

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
        Path fileWithHistoryPath = Path.of("test/FileBackedTest_2.csv");
        FileBackedTaskManager testManager = FileBackedTaskManager.load(fileWithHistoryPath.toFile());

        Assertions.assertEquals(3,testManager.getHistory().size());
        Assertions.assertEquals(List.of(testManager.getTask(1),
                        testManager.getEpic(3),
                        testManager.getSubtask(5)),
                testManager.getHistory());
    }
}
