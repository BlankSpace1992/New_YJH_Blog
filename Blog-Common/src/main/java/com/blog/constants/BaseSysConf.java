package com.blog.constants;

/**
 * 常量的基类
 *
 * @author yujunhong
 * @date 2021/5/28 17:49
 */
public class BaseSysConf {

    public final static String TITLE = "title";
    public final static String SUMMARY = "summary";
    public final static String NAME = "name";
    public final static String CONTENT = "content";
    public final static String AVATAR = "avatar";
    public final static String AVATAR_URL = "avatarUrl";
    public final static String BLOB = "blob";
    public final static String ERROR = "error";
    public static final String ADMIN = "admin";
    public static final String BLOG = "blog";
    public static final String EMAIL = "email";
    public static final String TEXT = "text";
    public static final String TO_TEXT = "to_text";
    public static final String TO_NICKNAME = "to_nickname";
    public static final String USER = "user";

    public static final  String DEFAULT_UID = "uid00000000000000000000000000000";
    public static final  String LIMIT_ONE = "LIMIT 1";
    public static final  String OID = "oid";
    public static final String NICKNAME = "nickname";

    /**
     * picture相关
     */
    public static final  String USER_UID = "userUid";
    public static final  String USER_NAME = "userName";
    public static final  String ADMIN_UID = "adminUid";
    public static final  String ROLE = "role";
    public static final  String PROJECT_NAME = "projectName";
    public static final  String SORT_NAME = "sortName";
    public static final  String PIC_NAME = "picName";
    public static final  String FILE_NAME = "fileName";
    public static final  String UPLOADED = "uploaded";
    public static final  String QI_NIU_URL = "qiNiuUrl";
    public static final  String PIC_URL = "picUrl";
    public static final  String MINIO_URL = "minioUrl";
    public static final  String URL = "url";
    public static final  String MESSAGE = "message";
    public static final  String EXPANDED_NAME = "expandedName";
    public static final  String FILE_OLD_NAME = "fileOldName";
    public static final  String PICTURE_LIST = "pictureList";
    public static final  String SOURCE = "source";


    /**
     * IP相关
     */
    public static final  String OS = "OS";
    public static final  String BROWSER = "BROWSER";
    public static final  String IP = "ip";
    public static final  String UTF_8 = "utf-8";


    public static final  String SUCCESS = "success";
    public static final  String STATUS = "status";
    public static final  String CREATE_TIME = "createTime";
    public static final  String UPDATE_TIME = "updateTime";
    public static final  String TOKEN = "token";
    public static final  String PLATFORM = "platform";
    public static final  String ACCESS_TOKEN = "accessToken";

    public static final  String CAN_NOT_COMMENT = "0";
    public static final  String CODE = "code";
    public static final  String DATA = "data";
    public static final  String UID = "uid";
    public static final  String PAGE_NAME = "pageName";
    public static final  String LEFT = "left";
    public static final  String RIGHT = "right";

    public static final  String DEFAULT_VALUE = "defaultValue";

    /**
     * platform平台相关
     */
    public static final  String WEB = "web";

    /**
     * 分页相关
     */
    public static final  String TOTAL = "total";
    public static final  String TOTAL_PAGE = "totalPage";
    public static final  String CURRENT_PAGE = "currentPage";
    public static final  String BLOG_LIST = "blogList";
    public static final  String PAGE_SIZE = "pageSize";

    /**
     * blog
     */
    public static final  String BLOG_UID = "blogUid";
    public static final  String LEVEL = "level";

    public static final  String START_EMAIL_NOTIFICATION = "startEmailNotification";


    /**
     * RabbitMQ的命令操作
     */
    public static final  String COMMAND = "command";
    public static final  String EDIT = "edit";
    public static final  String ADD = "add";
    public static final  String DELETE = "delete";
    public static final  String DELETE_BATCH = "deleteBatch";
    public static final  String EDIT_BATCH = "editBatch";
    public static final  String DELETE_ALL = "deleteAll";
    public static final  String EXCHANGE_DIRECT = "exchange.direct";
    public static final  String MOGU_BLOG = "mogu.blog";

    /**
     * redis相关
     */
    public static final  String BLOG_SORT_BY_MONTH = "BLOG_SORT_BY_MONTH";
    /**
     * redis分割符
     */
    public static final  String REDIS_SEGMENTATION = ":";
    public static final  String EQUAL_TO = "=";
    /**
     * 月份集合
     */
    public static final  String MONTH_SET = "MONTH_SET";
    /**
     * 博客等级
     */
    public static final  String BLOG_LEVEL = "BLOG_LEVEL";


    /**
     * 字典类型
     */
    public static final  String REDIS_DICT_TYPE = "REDIS_DICT_TYPE";

    /**
     * 文件分割符
     */
    public static final  String FILE_SEGMENTATION = ",";

    /**
     * 系统全局是否标识
     */
    public static final int YES = 1;
    public static final int NO = 0;

    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int FOUR = 4;
    public static final int FIVE = 5;
    public static final int SIX = 6;
    public static final int SEVEN = 7;
    public static final int EIGHT = 8;
    public static final int NINE = 9;
    public static final int TEN = 10;

    public static final int TWO_TWO_FIVE = 225;
    public static final int ONE_ZERO_TWO_FOUR = 1024;

    /**
     * SystemConfig相关
     */
    public static final  String UPLOAD_QI_NIU = "uploadQiNiu";
    public static final  String UPLOAD_LOCAL = "uploadLocal";
    public static final  String UPLOAD_MINIO = "uploadMinio";
    public static final  String LOCAL_PICTURE_BASE_URL = "localPictureBaseUrl";
    public static final  String QI_NIU_PICTURE_BASE_URL = "qiNiuPictureBaseUrl";
    public static final  String MINIO_PICTURE_BASE_URL = "minioPictureBaseUrl";
    public static final  String QI_NIU_ACCESS_KEY = "qiNiuAccessKey";
    public static final  String QI_NIU_SECRET_KEY = "qiNiuSecretKey";
    public static final  String QI_NIU_BUCKET = "qiNiuBucket";
    public static final  String QI_NIU_AREA = "qiNiuArea";

    public static final  String MINIO_END_POINT = "minioEndPoint";
    public static final  String MINIO_ACCESS_KEY = "minioAccessKey";
    public static final  String MINIO_SECRET_KEY = "minioSecretKey";
    public static final  String MINIO_BUCKET = "minioBucket";

    public static final  String PICTURE_PRIORITY = "picturePriority";
    public static final  String CONTENT_PICTURE_PRIORITY = "contentPicturePriority";
    public static final  String PICTURE = "picture";
    public static final  String PICTURE_TOKEN = "pictureToken";
    public static final  String LIST = "list";


    /**
     * AOP相关
     */
    public static final String AUTHOR = "author";
    public static final String BLOG_SORT_UID = "blogSortUid";
    public static final String TAG_UID = "tagUid";
    public static final String KEYWORDS = "keywords";
    public static final String MONTH_DATE = "monthDate";
    public static final String MODULE_UID = "moduleUid";
    public static final String OTHER_DATA = "otherData";
    public static final String COMMENT_VO = "commentVO";

    public static final String TARGET = "target";

    /**
     * 参数配置相关
     */
    public static final String SYS_DEFAULT_PASSWORD = "SYS_DEFAULT_PASSWORD";
    public static final String BLOG_NEW_COUNT = "BLOG_NEW_COUNT";
    public static final String BLOG_FIRST_COUNT = "BLOG_FIRST_COUNT";
    public static final String BLOG_SECOND_COUNT = "BLOG_SECOND_COUNT";
    public static final String BLOG_THIRD_COUNT = "BLOG_THIRD_COUNT";
    public static final String BLOG_FOURTH_COUNT = "BLOG_FOURTH_COUNT";
    public static final String BLOG_HOT_COUNT = "BLOG_HOT_COUNT";
    public static final String HOT_TAG_COUNT = "HOT_TAG_COUNT";
    public static final String FRIENDLY_LINK_COUNT = "FRIENDLY_LINK_COUNT";
    public static final  String PROJECT_NAME_ = "PROJECT_NAME";
    public static final  String MAX_STORAGE_SIZE = "MAX_STORAGE_SIZE";

    public static final String ALL = "all";

    public static final String PASS_WORD = "passWord";

    public static final String BLOG_COUNT = "blogCount";
    public static final String USER_COUNT = "userCount";
    public static final String COMMENT_COUNT = "commentCount";
    public static final String VISIT_COUNT = "visitCount";
    public static final String BLOG_COUNT_BY_TAG = "blogCountByTag";
    public static final String VISIT_BY_WEEK = "visitByWeek";

    public static final String ADMINUIDS = "adminUids";
    public static final String ROLEUIDS = "roleUids";
    public static final String CLAIMS = "claims";



    public static final String ROLES = "roles";

    public static final String PARENT_LIST = "parentList";
    public static final String SON_LIST = "sonList";
    public static final String BUTTON_LIST = "buttonList";


    public static final String LOG = "log";
    public static final String EXCEPTION = "exception";

    public static final String NICK_NAME = "nickName";
    /**
     * 评论相关
     */
    public static final String COMMENT_LIST = "commentList";
    public static final String REPLY_LIST = "replyList";

    /**
     * 用于第三方登录
     */
    public static final String GITHUB = "github";
    public static final String GITEE = "gitee";
    public static final String QQ = "qq";
    public static final String UUID = "uuid";
    public static final String OPEN_ID = "openId";
    public final static String MOGU = "MOGU";
    public final static String LOCATION = "location";
    public final static String GENDER = "gender";
    public final static String MALE = "male";
    public final static String FEMALE = "female";

    public static final String RECEIVER = "receiver";
    public static final String SUBJECT = "subject";

}
