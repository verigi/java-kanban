package task.manager;

import task.elements.Epic;
import task.elements.Subtask;
import task.elements.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefaultTask();
        //Создайте две задачи, эпик с двумя* подзадачами и эпик без подзадач.
        Task task1 = new Task("Задача 1", "...", Status.NEW);
        Task task2 = new Task("Задача 2", "...", Status.NEW);
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epicWithSubs = new Epic("Эпик с подзадачами", "...", Status.NEW);
        manager.addEpic(epicWithSubs);

        Subtask subtask1 = new Subtask("Подзадача 1", "...", Status.NEW, 3);
        Subtask subtask2 = new Subtask("Подзадача 2", "...", Status.NEW, 3);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        Epic epicWithoutSubs = new Epic("Эпик без подзадач", "...", Status.NEW);
        manager.addEpic(epicWithoutSubs);

        //Запросите созданные задачи несколько раз в разном порядке.
        manager.getTask(1);
        manager.getTask(1);
        manager.getTask(2);
        manager.getEpic(3);
        manager.getSubtask(4);
        manager.getSubtask(4);
        manager.getSubtask(5);
        //После каждого запроса выведите историю и убедитесь, что в ней нет повторов.
        System.out.println("История после запросов задач: " + manager.getHistory());
        //Удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться.
        manager.deleteTaskByID(1);
        System.out.println("История после запросов задач и удаления: " + manager.getHistory());
        //Удалите эпик с двумя подзадачами и убедитесь, что из истории удалился как сам эпик, так и все его подзадачи.
        manager.deleteEpicByID(3);
        System.out.println("История после запросов задач и удаления эпика: " + manager.getHistory());
    }
}
