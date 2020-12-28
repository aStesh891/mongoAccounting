package com.mongodb.app.consts;

public class Config {
  public static final class MONGO_DB {
    public static final String MONGODB_URI = "mongodb+srv://root:root@steshtoy.rgnxw.mongodb.net/<dbname>?ssl=true&authSource=admin&retryWrites=true&w=majority&maxIdleTimeMS=5000";
    public static final String DB_NAME = "steshenko";
    public static final String COLLECTION_NAME = "companies";
  }

  public static final class COMPANY_PARAMS {
    public static final String ID_PARAM = "_id";
    public static final String NAME_PARAM = "name";
    public static final String LINK_PARAM = "link";
    public static final String BLOG_URL_PARAM = "blogUrl";
    public static final String EMAIL_ADDRESS_PARAM = "emailAddress";
    public static final String PHONE_NUMBER_PARAM = "phoneNumber";
    public static final String NUMBER_OF_EMPLOYEES_PARAM = "numberOfEmployees";
    public static final String DESCRIPTION_PARAM = "description";
    public static final String PRODUCTS_PARAM = "products";
  }
}
