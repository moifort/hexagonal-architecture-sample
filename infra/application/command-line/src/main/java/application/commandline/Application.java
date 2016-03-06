package application.commandline;

import domain.filemanager.api.FileManagerService;
import domain.filemanager.api.entity.File;
import domain.filemanager.core.FileManagerServiceImpl;
import domain.filemanager.spi.FileEventNotification;
import domain.notificationmanager.api.NotificationManagerService;
import domain.notificationmanager.core.NotificationManagerServiceImpl;
import domain.notificationmanager.spi.FileEventNotificationService;
import notification.irc.IrcHandler;
import notification.mail.MailEventNotification;
import persistence.inmemory.repository.InMemoryRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Application {

    public static void main(String[] args) throws Exception {
        /***** Init *****/
        // Repository
        //FileRepository fileRepository = SQLPersistence.get();
        InMemoryRepository fileRepository = new InMemoryRepository();

        // Notification Service
        FileEventNotificationService mailNotificationService = new MailEventNotification("sender@gmail.com", "mypassword", "thibaut.mottet@gmail.com");
        FileEventNotificationService ircNotificationService = new IrcHandler("irc.freenode.org","#HewaBot");
        NotificationManagerService notificationManagerService = new NotificationManagerServiceImpl(
                Arrays.asList(mailNotificationService, ircNotificationService),
                fileRepository);

        // File Manager
        FileManagerService fileManagerService = new FileManagerServiceImpl(fileRepository, notificationManagerService);


        /***** Run *****/
        notificationManagerService.setUserSettingNotification("Thibaut",
                Collections.emptyList(),
                Collections.singletonList(FileEventNotification.Type.DELETE),
                true);
        System.out.println("Thibaut accept to receive delete file notification through all services");

        notificationManagerService.setUserSettingNotification("Thibaut",
                Collections.singletonList(ircNotificationService.getServiceId()),
                Collections.singletonList(FileEventNotification.Type.GET),
                true);
        System.out.println("Thibaut accept to receive get file notification through"+ ircNotificationService.getServiceId());

        File file = fileManagerService.addFile("test1.txt", "test1.txt".getBytes(), "Thibaut");
        System.out.println("Add new file   -> " + file);

        file = fileManagerService.getFile(file.getId(), "Thibaut");
        System.out.println("Get file id: " + file.getId() + " -> " + file);

        notificationManagerService.setUserSettingNotification("Thibaut",
                Collections.singletonList(mailNotificationService.getServiceId()),
                Collections.singletonList(FileEventNotification.Type.ADD),
                true);
        System.out.println("Thibaut accept to receive add file notification through"+ mailNotificationService.getServiceId());

        file = fileManagerService.addFile("test2.txt", "test2.txt".getBytes(), "Thibaut");
        System.out.println("Add new file   -> " + file);

        List<File> files = fileManagerService.getAllFiles("Thibaut");
        System.out.println("Get all files id: " + files);

        fileManagerService.deleteFile("1", "Thibaut");
        System.out.println("delete file id: " + 1);

        files = fileManagerService.getAllFiles("Thibaut");
        System.out.println("Get all files id: " + files);
    }
}
