# Practice_SPIIRAS_Task_1
Практическое задание АО «СПИИРАН-НТБВТ» 

Для запуска программы в docker нужно 
1. Cобрать весть проект в Code.jar и положить в Code/out/artifacts/Code_jar/Code.jar.
2. Выполнить команды:
   #### cd Code
   #### docker build -t app_task_1 .
   #### docker run --name App_task_1_container -d app_task_1
Программа сразу выполниться и выведет в Stdout/Logs результат работы.
