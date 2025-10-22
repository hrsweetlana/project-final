

## POST /register
- Викликається `RegisterController.register()`
- Створюється `ConfirmData`
- Публікується подія `RegistrationConfirmEvent`

## RegistrationListener.confirmRegistration()
- Створює `User` з DTO (`userMapper.toEntity()`)
- Відправляє email (`mailService.sendToUserAsync()`)

**Очікується клік по email**

## GET /register/confirm?token=...
- Обробляється `ConfirmController.confirm()`
- Користувач зберігається в базу (`userRepository.save(user)`)
- Перенаправлення на сторінку логіну



# Реєстрація користувача

**1. GET /ui/register**
   - записує `userTo` в модель
   - показує форму `unauth/register`

**2. POST /ui/register**
   - @Validated UserTo
   - if (result.hasErrors()) -> return `unauth/register`
   - `checkNew()` ->  getId() != null; -> IllegalRequestDataException
   - записує `token` в сесію 
   - публікується подія `new RegistrationConfirmEvent()`
   - слухач `MailListener.confirmRegistration()` 
   		- cтворює `User` з DTO
   		- (користувача ще не записано в БД)
   		- формує email
   		- валідує пошту
   		- надсилає лист `MailService.sendToUserAsync` 
   		- `if (appConfig.isProd() || !appConfig.isTest())` -> відравляє на пошту
       `else` тестова пошта (з дефолтного профілю прописуємо в формі будь-яку пошту і пароль, але відправлятись буде на те, що прописано для поштового сервера(тестовапошта@gmail.com):
    properties:
      mail:
        smtp:
          starttls:
            enable: true
          auth: true
    host: smtp.gmail.com
    username: тестовапошта@gmail.com
	mailpassword:(згенерувати в gmail))
       - після заповнення форми для користовача оновлюється сторінка на /login
        якщо успіх, тоді 
   		- з листа пореходимо на 3. GET `/ui/register/confirm?token=...`; (але воно напевно не асинхронно, бо нема анотації на класі)}
   		- помилка, тоді запис в БД `mailCaseRepository.save(new MailCase())`
   		
**3. GET /ui/register/confirm?token=...**
   	 - приймає і валідує токен
   	 - якщо link сформований `prepareForSave != null` (передається в конструктор  private final Function<E, E> prepareForSave)
   	 - зберігає користувача в БД `handler.createFromTo(confirmData.getUserTo())`
   	 - закриває сесію
   	 - повертає `login` 
   	 
# Перегляд і редагування Profile

**1. GET /ui/profile**

  - бере ID зареєстрованого користувача `AbstractProfileController.get(authUser.id())`  
  - якщо профілю для користувача ще нема, то він створюється: `profileRepository.getOrCreate(id): Profile`
  - поле `Set<@NotBlank String> mailNotifications` наповнюється значеннями з BD(швидше за все порожній список) `ProfileUtil.maskToNotifications(entity.getMailNotifications()//тут швидше за все 0???, але це перший раз, якщо вже до того щось було вибрано, тоді з моделі береться "profile" і його значення mailNotification)` витягуючи з таблиці Rеference за значенням RefType `ReferenceService.getRefs(RefType.MAIL_NOTIFICATION)`
  - `profileMapper.toTo(profileRepository.getOrCreate(id)):ProfileTo` мапить той порожній список з Profile на ProfileTo
  - записує отриманий ProfileTo в модель `model.addAttribute("profile", super.get(authUser.id()))`
  - записує тип можливих контактів з таблиці Reference в модель `model.addAttribute("contactRefs", ReferenceService.getRefs(RefType.CONTACT).values())`
  - записує тип можливих нотифікацій з таблиці Reference в модель `model.addAttribute("mailNotificationRefs", ReferenceService.getRefs(RefType.MAIL_NOTIFICATION).values())`
  - повертає сторінку 'profile'


**2. POST /ui/profile**

  - якщо помилка `result.hasErrors()` в модель кладеться список помилок `redirectAttrs.addFlashAttribute("profileError", errorMessageHandler.getErrorList(result))`, далі редирект на '/ui/profile' і друк помилок на сторінці
  - якщо валідація даних успішна, то в модель записується статус успіх `redirectAttrs.addFlashAttribute("profileSuccess", "Saved successfully")`
    - `AbstractProfileController.update(profileMapper.fromPostToTo(profile), authUser.id())`:
      - `ProfilePostRequest` мапиться до `ProfileTo` і передається в `update()`
      - якщо поля `Contacts[] = null` , то використовуються дефолтні значення `NullValuePropertyMappingStrategy.SET_TO_DEFAULT`, тобто порожній масив в ProfileMapper.fromPostToTo()
      - `ValidationUtil.assureIdConsistent(profileTo, id)`:
        - якщо `ProfileTo` новий об'єкт і не має `id`, то воно сетається з `authUser.id()`
        - `Profile.id != authUserId`, тоді `IllegelRequestDataException`, тобто чи належить профайл юзеру
      - `ValidationUtil.assureIdConsistent(profileTo.getContacts(), id)` тобто чи `contact.id = userId`
      - `ProfileUtil.checkContactsExist(profileTo.getContacts())`  чи в `Contact` icнує такий код `ReferenceService.getRefTo(RefType.CONTACT, c.getCode()))`
      - `ProfileTo` мапиться до `Profile ProfileMapper.updateFromTo()`, і сетається `id` контактам `contactToSetToContactSet()` в `ProfileMapperImpl`
      - далі редирект на '/ui/profile' і друк "успішної операції" на сторінці і відображення оновлених даних на сторінці


**2а. POST /api/profile**

  - якщо валідація даних успішна, то `AbstractProfileController.update(profileTo, authUser.id())`
    - `ValidationUtil.assureIdConsistent(profileTo, id)`
      - якщо `ProfileTo` новий об'єкт і не має `id`, то воно сетається з `authUser.id()`
      - `Profile.id != authUserId`, тоді `IllegelRequestDataException`, тобто чи належить профайл юзеру
    - `ValidationUtil.assureIdConsistent(profileTo.getContacts(), id)`, тобто чи `contact.id = userId`
    - `ProfileUtil.checkContactsExist(profileTo.getContacts())`  чи значення поля в `ContactTo` не порожнє `@NotBlank value` Якщо об’єкт не проходить перевірку, кидається ConstraintViolationException і чи в Contact icнує такий код ReferenceService.getRefTo(RefType.CONTACT, c.getCode())), якщо нема - в Util.notNull() кидається IllegalArgumentException, який обробляється в BasicExceptionHandler put(IllegalArgumentException.class, ErrorType.BAD_DATA); ErrorType.BAD_REQUEST("Bad request", HttpStatus.UNPROCESSABLE_ENTITY), і викликається з RestExceptionHandler;
    - ProfileTo мапиться до Profile ProfileMapper.updateFromTo(), в ProfileTo первірка чи поле не порожнє `@NotBlank mailNotifications`; `ProfileUtil.notificationsToMask(to.getMailNotifications()))` перевіркиа на валідність `notifications`: перевіряється `RefType`, якщо такого нема, тоді `IllegalArgumntException`, і сетається `id` контактам `contactToSetToContactSet()` в `ProfileMapperImpl`
    - далі зберігання оновленого профілю `profileRepository.save(profile)`;
    
    
# Перегляд і редагування task

**1. GET /api/tasks/{id}**

  - отримує id проекту, викликає `TaskServise.getId(id)`
  - якщо таке значення існує в репозиторії `Util.checkExist(id, handler.getRepository().findFullById(id))` повертає Task
  - task мапиться до taskToExt, в TaskFullMapperImpl полям яких нема в Task присвоюється null(`String description = null; String priorityCode = null; LocalDateTime updated = null; Integer estimate = null; List<ActivityTo> activityTos = null;`зберігатися дані з цих полів будуть в activity. А в task навпаки є поля  startpoint, endpoint,(@Mapper(config = TimestampMapper.class)) activities, tags)
  - За id таски отримуємо список активностей `List<Activity> activities = activityHandler.getRepository().findAllByTaskIdOrderByUpdatedDesc(id)`
  - TaskUtil.fillExtraFields(taskToFull, activities) якщо є в activity заповнені поля description, priorityCode, updated, estimate, activityTos, то вони засетаються в taskToFull
  - в taskToFull сетається поле activities `taskToFull.setActivityTos(activityHandler.getMapper().toToList(activities))`  
  - taskToFull
  
 **2. GET api/tasks/by-project/**
   -taskHandler.getMapper() по id проекту мапить список тасків до списку to тасків і повертає List<TaskTo>
 
 
 **3. POST /api/tasks/**
 
   - викликається трансакційний метод метод `taskService.create(TaskToExt taskTo))`;
   -  з Handlers.TaskExtHandler викликається handler.createWithBelong(taskTo, TASK, "task_author")
     - E created = baseHandler.createFromTo(TaskToExt taskTo) перевіряється чи об'єкт новий, тобто чи в таски є id `ValidationUtil.checkNew(taskTo)`. TO мапиться до об'єкту Task в `baseMapper.toEntity(taskTo)`
     - збарігаємо об'єкт в репозиторії `taskRepository.save(task)` з CodeTo в TaskToFull сетаються Long parentId, long projectId, Long sprintId в Task
     - `createUserBelong(task.id(), TASK, AuthUser.authId(), task_author)`, якщо `belongRepository.findActiveAssignment(id, TASK, userId, "task_author").isEmpty())`,тобто в UserBelongRepository нема об'єкту UserBelong з id таски і автором,  то створюємо новий об'єкт UserBelong `new UserBelong(id, TASK, userId, "task_author")` і зберігаємо в репозиторії `belongRepository.save(belong)`
     - makeActivity(created.id(), taskTo) -> створює нову активність new Activity() четвертим параметром приймає null(updated), але   '@UpdateTimestamp @Nullable @Column(name = "updated") private LocalDateTime updated', тому перед кожним оновленням Task  поле оновиться часом зміни (при Put використовується об'єкт TaskToExt -> )
     - `activityHandler.create(makeActivity(created.id(), taskTo))` - приймає параметром активність і зберігає її в репозиторії ActivityRepository;
       - якщо (prepareForSave != null), тобто taskRepository != null  entity = prepareForSave.apply(entity) ??
       - 
     - повертаємо task (E created)
  - поветаємо ResponseEntity<Task>, з статусом created()
  
  
  **4. PATCH /api/tasks/{id}/change-status**
  
  - викликається трансакційний метод `taskService.changeStatus(id, statusCode)`
    - `Assert.notNull` -> перевіряє, що вхідний параметр statusCode не null, в іншому випадку IllegalArgumentException, хоча в контролері вже є перевірка на @NotBlank @RequestParam String statusCode
    - За id таски отримуємо отримуємо об'єкт task -> `Task task = handler.getRepository().getExisted(taskId)`
    - якщо новий статус код відмінний від встановленого в task `!statusCode.equals(task.getStatusCode())`
      - тоді перевіряється чи такий статус можливо встановити 'task.checkAndSetStatusCode(statusCode)', новий статус і текучий статус передаються в метод    'checkStatusChangePossible(this.statusCode, statusCode)', якщо можливо,  то він зміниться на новий 
        - TaskUtil.getPossibleStatuses(currentStatus) поверне Set<String> можливих статусів для текучого статусу. 
          - `Map<String, RefTo> taskStatusRefs = getRefs(TASK_STATUS)`, де enum RefType = TASK_STATUS.
            - `Map<String, RefTo> getRefs(RefType refType)` з ReferenceService викликає `getExisted(refSelect, refType)` з Util, де `static Map<RefType, Map<String, RefTo>> refSelect` статична мапа, яка ініціалізується на початку роботи програми методом loadReferences(). Цей метод витягне з бази об'єкти References, але замапить їх mapper.toToList до списку трансфер об'єктів List<RefTo>; в цьому методі викликається `Util.getExisted(refSelect, refType)`, де `getExisted(Map<K,   V> map, K key) refSelect` це сама мапа, а refType це    TASK_STATUS, тобто з мапи вигляду : `static Map<RefType, Map<String, RefTo>> refSelect` `(TASK_STATUS={todo=RefTo(refType=TASK_STATUS, aux=in_progress,canceled|, splittedAux=[in_progress,canceled]))` по ключу RefType(TASK_STATUS), ми витягуємо підмапу  `Map<String, RefTo>` :
           `todo=RefTo(refType=TASK_STATUS,            aux=in_progress,canceled|,                               splittedAux=[in_progress,canceled]), 
	        in_progress=RefTo(refType=TASK_STATUS,      aux=ready_for_review,canceled|task_developer,           splittedAux=[ready_for_review,canceled, task_developer]), 
			 ready_for_review=RefTo(refType=TASK_STATUS, aux=in_progress,review,canceled|,                        splittedAux=[in_progress,review,canceled]),
			 review=RefTo(refType=TASK_STATUS,           aux=in_progress,ready_for_test,canceled|task_reviewer,   splittedAux=[in_progress,ready_for_test,canceled, task_reviewer]), 
			 ready_for_test=RefTo(refType=TASK_STATUS,   aux=review,test,canceled|,                               splittedAux=[review,test,canceled]), 
			 test=RefTo(refType=TASK_STATUS,             aux=done,in_progress,canceled|task_tester,               splittedAux=[done,in_progress,canceled, task_tester]), 
			 done=RefTo(refType=TASK_STATUS,             aux=canceled|,                                           splittedAux=[canceled]), 
			 canceled=RefTo(refType=TASK_STATUS,         aux=null,                                                splittedAux=null)`
	      - `String aux = taskStatusRefs.get(currentStatus).getAux(0)` , витягуємо з мапи нульовий елемент масиву splitedAux(це поле є тільки в RefTo. в Reference його нема), в якому є всі можливі статуси. В масиві splitedAux, стрічка aux поділиться по or -> aux=ready_for_review,canceled|task_developer, буде splitedAux[0]=ready_for_review,canceled і splitedAux[1]=task_developer
	      - `possibleStatuses.addAll(aux == null ? Set.of() : Set.of(aux.split(",")))`, 
	    - повертаємо Set<String> можливих статусів (canceled|task_reviewer використовується нище в `handler.createUserBelong()`  
	   - якщо статус змінити неможливо, тоді `throw new DataConflictException("Cannot change task status from " + currentStatus + " to " + newStatus)`
    - створюється нова активність, туди сетається новий statusCode `Activity statusChangedActivity = new Activity(null, taskId, AuthUser.authId()); statusChangedActivity.setStatusCode(statusCode)`
    - активнісь зберігається в бд `Activity statusChangedActivity = new Activity(null, taskId, AuthUser.authId()) statusChangedActivity.setStatusCode(statusCode)`
    - якщо в активності є юзер `if (userType != null) { handler.createUserBelong(taskId, TASK, AuthUser.authId(), userType)`, то він зберігається в UserBelongRepository