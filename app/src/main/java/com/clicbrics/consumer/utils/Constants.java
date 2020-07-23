package com.clicbrics.consumer.utils;

import android.os.Environment;

/**
 * Created by Paras on 15-09-2016.
 */
public class Constants {


    public static final String UPDATE_MORE = "com.clicbrics.clicbrics.homescreen.morefrag";
    public static final String MORE_FRAGMENT_UPDATE = "more_fragment_update";
    public static String isUserPropertySet="is_UserPropertySet";


    public interface AppConfigConstants{
        String MAP_PROJECT_COUNT = "map_project_count";
        String MAP_PROJECT_REF_DISTANCE = "map_project_ref_distance";
        String DEFAULT_INTEREST_RATE = "default_interest_rate";
        String CONTACT_NUMBER = "contact_number";
        String PARAM_UPDATE_APP ="PARAM_UPDATE_APP";
    }

    public interface ActivityRequestCode{
        int FAVORITE_ACT_RESULT_CODE  = 433;
        int RECENT_ACT_RESULT_CODE  = 434;
        int SAVE_SEARCH_RESULT_CODE = 444;
    }

    public interface AppConstants {
        int ALERTOR_LENGTH_LONG = 1500;
        int ALERTOR_LENGTH_MEDIUM = 1000;
        int ALERTOR_LENGTH_SMALL = 500;
        float DEFAULT_INTEREST_RATE = 8.6f;
        int TENURE_SEEK=5;
        String HOLD_TIME = "Hold_time_in_seconds ";
        String UPDATE_STAT_TIME = "update_stat_time";
        String SOURCE = "AndroidApp";
        String VIRTUAL_UID = "virtual_uid";
        String PROFILE_IMAGE_PATH = Environment
                .getExternalStorageDirectory().getPath() + "/profilePic";

        enum PropertyStatus {
            NotStarted, InProgress, ReadyToMove,Resale,UpComing;
        }

        String IS_NEWS_HELP_SCREEN_SEEN = "is_news_help_screen_seen";
        String IS_ARTICLE_HELP_SCREEN_SEEN = "is_article_help_screen_seen";
        String IS_DECOR_HELP_SCREEN_SEEN = "is_article_help_screen_seen";

        /*enum PropertyType {
            Apartment, IndependentHouseVilla, IndependentFloor, Studio, Duplex, Penthouse, RowHouse, Land, Shop;
        }*/

        enum PropertyType {
            Apartment,IndependentHouseVilla, IndependentHouse,Villa, IndependentFloor, Studio, Duplex, Penthouse, RowHouse, Land, Shop,
            OfficeSpace, Warehouse, IndustrialBuilding, Hotel, GuestHouse, CommercialLand, InstitutionLand, IndustrialLand, AgricultureLand;
        }

        enum OSType {
            ANDROID, IPHONE
        }

        enum AppType {
            Consumer, Agent
        }

        enum LoginType {
            Housing, Google, Facebook;
        }

        enum ServiceRequestType{
            Booking,Cancellation,Transfer,Possession,ConstructionUpdate,Other
        }

        int STORAGE_PERMISSION = 999;

        int SPLASH_TIME = 1000;
        String CRORE="Cr";
        String LAKHS="L";
        String THOUSAND = "K";
        String LOGIN_TYPE_PREF_KEY = "login_type_pref_key";
        String USER_NAME_PREF_KEY = "name_pref_key";
        String EMAIL_PREF_KEY = "email_pref_key";
        String MOBILE_PREF_KEY = "mobile_pref_key";
        String PASS_PREF_KEY = "pass_pref_key";
        String IMAGE_PREF_KEY = "image_pref_key";
        String BHK = " BHK";
        long MAP_PROPERTY_RADIUS = 15000l;
        long MAP_PROPERTY_MOVE_RADIUS = 12000l;
        float MIN_ZOOM_LEVEL = 11;
        double DEFAULT_LAT = 28.45949935913086;
        double DEFAULT_LONG = 77.02660369873047;
        int WAIT_FOR_LOCATION_MIN_SEC = 500;
        String OTP_SMS_BROADCAST = "android.provider.Telephony.SMS_RECEIVED";
        String LOCATION = "location";
        String HOME = "home";

        String SUCCESS = "success";
        String FAILED = "failed";
        String ACTION_BAR_TITLE = "action_bar_title";
        String EDIT_FIELD_IS_ENABLE = "edit_field_is_enable";
        String IS_FROM_PROFILE = "is_from_profile";
        String SELECTED_COUNTRY_NAME = "selected_counury_name";
        int RIDER_IMAGE_COMPRESSION_RATIO = 100;
        String START_Up = "start_up";
        int CONTACT_SELECTED_REQUEST_ID = 1001;
        String IS_PASSWORD_AVAILABLE_PREF_KEY = "IS_PASSWORD_AVAILABLE_PREF_KEY";
        String SAVED_SEARCHES = "saved_search";
        String NOTIFICATIONS = "notifications";
        String PROJECT_ID_SET = "project_id_list";
        String SAVED_SEARCH_COUNT = "saved_search_count";
        String PROFILE_PIC = "PROFILE_PIC.jpeg";
        String IMAGE_URL = "image_url";
        String COUNTRY_FLAG_DIR = "flags";
        String AMENITY_ICON_DIR = "amenities";
        String SELECTED_COUNTRY_FLAG_NAME = "selected_country_flag_name";
        String SELECTED_COUNTRY__NAME = "selected_country_name";
        String SELECTED_COUNTRY_STD_CODE = "selected_counury_std_code";
        String PROJECT_AND_BUILDER_LIST = "project_and_builder_list";
        String Top_Locality_LIST = "top_locality_list";
        String SELECTED_PROPERTY_UNIT = "selected_property_unit";

        public static final int EDIT_ACTIVITY_RESULT = 5000;
        String OVERVIEW_STRING = "PROJECT";
        String AMENITIES_STRING = "AMENITIES";
        String NEARBY_STRING = "NEARBY";

        String[] TIME_LIST = {" 8:00 AM", " 9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
                " 1:00 PM", " 2:00 PM", " 3:00 PM", " 4:00 PM", " 5:00 PM", " 6:00 PM", " 7:00 PM", " 8:00 PM",
                " 9:00 PM", "10:00 PM"};
        String[] TIME_VALUES = {"8:00:00", "9:00:00", "10:00:00", "11:00:00", "12:00:00",
                "13:00:00", "14:00:00", "15:00:00", "16:00:00", "17:00:00", "18:00:00", " 19:00:00", " 20:00:00",
                " 9:00 PM", "10:00 PM"};

        int NO_SELECTION = -1;
        String SAVED_CITY = "saved_city";
        String SAVED_STATE = "state";
        String SAVED_CITY_ID = "city_id";
        String SAVED_PNAME = "saved_pname";

        String ESTIMATE_CITY = "estimate_city";
        String ESTIMATE_STATE = "estimate_state";
        String ESTIMATE_CITY_ID = "estimate_city_id";
        String ESTIMATE_PNAME = "estimate_pname";


        String CITY_LIST = "city_list";
        String ESTIMATE_CITY_LIST = "estimate_city_list";
        String CITY_ID_LIST = "city_id_list";
        String NOTIFICATION_LIST = "notification_list";

        String UPDATE_FAVORITES = "update_favorites";

        String TAG_MY_PROPERTY_FRAGMENT = "tag_my_property_fragment";
        String TAG_SITE_VISITS_FRAGMENT = "tag_site_visits_fragment";
        String TAG_MY_PROPERTY_DETAILS_FRAGMENT = "tag_my_property_details_fragment";
        String TAG_PAYMENT_HISTORY_FRAGMENT = "tag_payment_history_fragment";
        String TAG_PROPERTY_DOCUMENT_FRAGMENT = "tag_property_document_fragment";
        String TAG_SERVICE_REQUEST_FRAGMENT = "tag_service_request_fragment";
        String TAG_SERVICE_REQUEST_HISTORY_FRAGMENT = "tag_service_request_history_fragment";
        String SHARE_BASE_URL = "https://www.clicbrics.com/project/";//housingwebappdev.appspot.com/project/";
        String IS_NEW_NOTIFICATION = "is_new_notification";
        String TAG_FAVORITE_FRAGMENT = "tag_favorite_fragment";
        String TAG_PICKCITY_FRAGMENT = "tag_pickcity_fragment";


        String NETWORK_ERROR = "network_error";
        String BACKEND_ERROR = "backend_error";

        String ABOUT_US_URL = "https://www.clicbrics.com/about-us-mobile.html";
        String T_AND_C_URL = "https://www.clicbrics.com/tnc-mobile.html";
        String PRIVACY_POLICY_URL = "https://www.clicbrics.com/privacy-policy-mobile.html";
        String USED_LIBRARY_URL = "https://www.clicbrics.com/libraries-mobile.html";
        String DEFAULT_NUMBER = "+91 80108 20000";
        String APP_TYPE = "AndroidApp";

        String PROJECT_LIST_FRAGMENT = "project_list_fragment";
        String HOME_DECOR_IDEAS_FRAGMENT = "home_decor_ideas_fragment";
        String NEWS_FRAGMENT = "news_fragment";
        String CLICWORTH_FRAGMENT = "clicworth_fragment";
        String ARTICLE_FRAGMENT = "article_fragment";
        String MORE_FRAGMENT = "more_fragment";
        String MAP_FRAGMENT = "map_fragment";

    }

    public interface BroadCastConstants{
        String FAVORITE_CHANGE = "com.clicbrics.consumer.favoritechange";
        String FAVORITE_CHANGE_MAP = "com.clicbrics.consumer.favoritechange.map";
        String ON_UNIT_CHANGE = "com.clicbrics.consumer.onunitchange";
    }

    public interface SavedSearchConstants{
        String IS_SAVE_SEARCH = "is_save_search";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String ADDRESS = "address";
        String ADDITIONAL_ADDRESS = "additional_address";
        String BUILDER_ID = "builder_id";
        String BUILDER_NAME = "builder_name";
        String CITY_ID = "city_id";
        String CITY_NAME = "city_name";
        String SEARCH_WRAPPER = "search_wrapper";
        String SEARCH_PROPERTY_BY_CITY = "search_property_by_city";
        String SEARCH_NAME = "search_name";
    }

    public interface SharedPreferConstants{
        String SORT_TYPE_RELEVANCE = "sort_by_relevance";
        String SORT_TYPE_LOW_TO_HIGH_PRICE = "sort_type_low_to_high_price";
        String SORT_TYPE_HIGH_TO_LOW_PRICE = "sort_type_high_to_low_price";
        String SORT_TYPE_LOW_TO_HIGH_AREA = "sort_type_low_to_high_area";
        String SORT_TYPE_HIGH_TO_LOW_AREA = "sort_type_high_to_low_area";
        String PARAM_FILTER_APPLIED = "filter_applied";
        String PARAM_BATH = "PARAM_BATH";
        String PARAM_MIN_AREA = "MIN_AREA";
        String PARAM_MAX_AREA = "MAX_AREA";

        String FIRST_LAUNCH = "first_time";  //App launched first time and a city and requiremnt is selcted;
        String PARAM_APARTMENT ="PARAM_APARTMENT";
        String PARAM_VILLA ="PARAM_VILLA";
        String PARAM_PLOTS ="PARAM_PLOTS";
        String PARAM_COMMERCIAL ="PARAM_COMMERCIAL";
        String PARAM_FLOOR ="PARAM_FLOOR";
        String PARAM_MIN_PRICE ="PARAM_MIN_PRICE";
        String PARAM_MAX_PRICE ="PARAM_MAX_PRICE";
        String PARAM_ROOM ="PARAM_ROOM";
        String PARAM_NEW ="PARAM_NEW";
        String PARAM_ONGOING ="PARAM_ONGOING";
        String PARAM_READY ="PARAM_READY";
        String PARAM_UPCOMING ="PARAM_UPCOMING";
        String PARAM_WELCOME_URL_PREF_KEY="WELCOME_URL_PREF_KEY";

        String CONCEIRGE_PHOTO_URL = "conceirge_photo_url";
        String CONCEIRGE_NAME = "conceirge_name";

        String TIME_STAMP = "timestamp";
        String IS_NOTIFICATION_SEEN = "is_notification_seen";

        String NOTIFICATION_REFRESH_TIME = "notification_refresh_time";
        String IS_MAP_HELPSCREEN_SEEN = "is_map_helpscreen_seen";
        String IS_SEARCH_RESULTS_HELPSCREEN_SEEN = "is_search_results_helpscreen_seen";
        String IS_FROM_SAVED_SEARCH = "is_from_saved_search";
        String IS_USING_DL = "is_using_dl";
        String RM_RATING = "rm_rating";
        String CITY_LIST_FETCH_TIME = "city_list_fetch_time";
        String ESTIMATE_CITY_LIST_FETCH_TIME = "estimate_city_list_fetch_time";



        String RECENT_PROJECT_LIST = "recent_project_list";
        String POLYGON_LATLNG_LIST  = "polygon_latlng_list";
        String SAVE_SEARCH_APPLIED = "save_search_applied";
    }

    public interface IntentKeyConstants{
        String PROPERTY_LIST = "property_list";
        String PROJECT_ID ="project_id";
        String ITEM_POSITION = "item_position";
        String PROJECT_NAME = "name";
        String PROPERTY_TYPE ="property_type";
        String PROPERTY_ID ="property_id";
        String PROPERTY_VALUE ="property_value";
        String URL = "url";
        String IMAGE_POSITION = "image_position";
        String IMAGE_DESCRIPTION = "description";
        String PROJECT_COVER_IMAGE = "project_cover_image";
        String IS_VIDEO = "isVideo";
        String VIDEO_URL = "video_url";
        String TASK_CATEGORY = "task_category";
        String FIRST_TIME = "first_time";
        String CITY_NAME = "city_name";
        String STATE_NAME = "state_name";
        String PAGE_INDEX = "page_index";
        String ID = "id";
        String FLOOR_PLAN = "floor_plan";
        String PRICE_RANGE = "price_reange";
        String BED_LIST = "bed_list";
        String ADDRESS = "address";
        String AREA_RANGE = "area_range";
        String OFFER = "offer";
        String IS_COMMERCIAL = "is_commercial";
        String BUILDER_NAME = "builder_name";
        String CALL_ME_BACK_REQUEST = "call_me_back";
        String IS_FROM_PROJECT_DETAIL = "is_from_project_detail";
        String TYPE = "type";
        String PROPERTY_SIZE = "property_size";
        String INTEREST_RATE =  "interest_rate";
        String POSITION = "position";
        String BANK_DETAILS = "bank_details";
        String IS_BANK_DETAILS_AVAIL = "is_bank_details_avail";
        String IS_FROM_LAYOUT = "is_from_layout";
        String IS_FROM_FINANCE = "is_from_finance";
        String IS_FROM_DETAIL_LINK = "is_from_detail_link";
        String IS_DEEPLINK = "is_deeplink";
        String PUSH_NOTIFICATION = "push_notification";
        String SHOW_PROPERTY_BY_CITY = "show_property_by_city";
        String PROJECT_STATUS = "project_status";
        String PROJECT_BSP_RANGE = "project_bsp_range";
        String BHK_TYPE = "bhk_type";

        String PROPERTY_SEARCH_BY_DEVELOPER = "property_search_by_developer";
        String PROPERTY_SEARCH_BY_LOCATION = "property_search_by_location";
        String IMAGE_LIST = "image_list";
        String NOTIFICATION_TYPE = "notification_type";
        String RESULT_CHANGE_CITY = "change_City";

    }

    public interface ServerConstants{
        String USER_ID ="userid";
        String PASSWORD ="pwd";
        String APP_NAME ="appName";
        String NAME = "name";
        String EMAIL = "email";
        String MOBILE = "mobile";
        String LOGIN = "login";
    }

    public static enum ServiceRequestType{
        Booking,Cancellation,Transfer,Possession,ConstructionUpdate,Other
    }

    public static enum TaskStatus{
        WaitingForAssign,open, inProgress, done , closed, cancel
    }

  /*  public static enum MessageType{
        Notification,
        PrivateNotification,
        News,
        Articles,
        HomeDecor,
        PropertyBooking
    }*/
  public static  enum LeadSource{
      Website, IosApp, AndroidApp, ThirdParty, Others, Agent,Concierge, Magicbricks, Email,
      Facebook, Sms, Microsite, Google, Linkedin, NewsPaper, CustomerReferral, Event, ChannelPartner,
      Leaflets, FBComments, SMSEx, OutBound, WhatsApp, ZeeMedia, TOI, Taboola
  }
    public static enum MessageType{
        TaskCreate,
        TaskUpdate,
        TaskDone,
        TaskReschedule,
        TaskAssign,
        TaskCancel,
        IncomingCall,
        NewLead,
        NewProject,
        UpdateProject,
        CallEnded,
        TargetUpdate,
        Notification,
        PrivateNotification,
        PropertyBooking,
        Login,
        AgentStatusUpdate,
        OutGoingCall,
        News,
        Articles,
        HomeDecor,
        NewEnquiry,
        SiteVisit
    }


    public static enum PriceSort{
        LowToHigh, HighToLow
    }

    public static enum PropertyBookingStatus{
        Hold, Requested,Booked,Approved,WaitingForAllotment,AllotmentDone, Canceled, TransferedFrom, TransferedTo,UserRequset
    }

    public static enum BlogStatus{
        Draft, Publish
    }

    public static enum FurnishedType {
        UnFurnished, SemiFurnished, FullFurnished;
    }

    public static enum PropertyUnit{
        SQFT, SQYD, SQM
    }

    public static enum ProjectType {
        Freehold, Leased
    }

    public enum SortType{
        Relevance,PriceASC,PriceDESC,AreaASC,AreaDESC
    }

    public interface ImageCaptureConstants{
        int PICKFILE_REQUEST_CODE = 1;
        int START_CAMERA_REQUEST_CODE = 2;
        String OPEN_INTENT_PREFERENCE = "selectContent";
        String IMAGE_BASE_PATH_EXTRA = "ImageBasePath";
        int OPEN_CAMERA = 4;
        int OPEN_MEDIA = 5;
        String SCANNED_RESULT = "scannedResult";
        String IMAGE_PATH = Environment
                .getExternalStorageDirectory().getPath() + "/scanSample";

        public final static String SELECTED_BITMAP = "selectedBitmap";
    }



    public interface AnalyticsProperty{
        String USER_ID = "UserId";
        String EMAIL_ID = "EmailId";
        String CITY_NAME = "CityName";
        String IMEI_NO = "imeiNo";
        String ROLE = "role";
        String DISPLAY_ROLE = "displayRole";
        String EMP_TYPE = "empType";
        String USER_NAME = "UserName";
        String OS_TYPE = "osType";
        String USER_PHONE = "UserPhone";
        String APP_VERSION = "appVersion";
        String USER_STATUS = "UserStatus";
        String USER_ADDRESS = "UserAddress";
        String COUNTRY_NAME = "CountryName";
        String COUNTRY_STDCODE = "CountrySTDCode";
        String ADHAAR_NO = "AdhaarNo";
        String COMPANY = "Company";
        String ADDRESS_PROOF_TYPE= "AddressProofType";
        String ADDRESS_PROOF_URL= "AddressProofUrl";
        String ADHAR_URL= "AdhaarUrl";
        String CHILDREN= "Children";
        String DESIGNATION= "Designation";
        String DOB= "Dob";
        String FATHERNAME= "FatherName";
        String FAX= "Fax";
        String FCM_KEY= "Fcmkey";
        String FORM_60= "Form60";
        String GENDER= "Gender";
        String IMAGE_URL= "ImageURL";
        String KYC_COMPANY= "KycCompany";
        String LATITUDE= "Latitude";
        String LONGITUDE= "Longitude";
        String LOGIN_TYPE ="LoginType";
        String MARITAL_STATUS="MaritalStatus";
        String MemorandumOfArticle="MemorandumOfArticle";
        String MPIN="MPIN";
        String NATIONALITY="Nationality";
        String NRI_DECALARATION="NriDeclarationForm";
        String OFFICE_ADDRESS="OfficeAddress";
        String OFFICE_PHONE="OfficePhone";
        String OTHER_EMAIL="OtherEmail";
        String OTHER_PHONE="OtherPhone";
        String PAN_NO="PanNo";
        String PAN_URL="PanUrl";
        String PASSPORT="Passport";
        String PASSWORD="Password";
        String PERMANENT_ADDRESS="PermanentAddress";
        String PERMANENT_CITY="PermanentCity";
        String PERMANENT_PIN="PermanentPin";
        String PERMANENT_STATE="PermanentState";
        String PROFESSION="Profession";
        String RESIDENTPHONE="ResidencePhone";
        String RESIDENTSTATUS="getResidentialStatus";
        String RESIDENT_TYPE="ResidentType";
        String SPOUSE_NAME="SpouseName";
        String STATE="State";
        String ThirdPartyID="ThirdPartyID";
        String TIME="Time";
        String USER_TYPE="UserType";
    }

    public interface DateFormat{
        String COMPLETE_DATE_FORMAT = "dd-MM-yyyy HH:mm:ss a";
        String DATE_FORMAT1 = "HH:mm a";
    }

    public interface AnalyticsEvents
    {
        String FAILED = "Failed";
        String SUCCESS = "Success";
        String ERROR_MESSAGE = "ErrorMessage";
        String API_NAME = "ApiName";
        String SCREEN_NAME = "ScreenName";
        String TIME_IN_MILIS = "TimeInMilis";
        String TIME = "Time";
        String STATUS = "Status";
        String USAGE_STATS = "UsageStats";
        String START_TIME = "StartTime";
        String EXIT_TIME = "ExitTime";
        String DURATION = "Duration";
        String DURATION_IN_MILIS = "DurationInMilis";
        String ON_CLICK_ACTION = "OnClickAction";
        String API_CALL = "ApiCall";
        String ACTION_NAME = "ActionName";
        String LATITUDE="Latitude";
        String LONGITUDE="Longitude";
        String CITY="City";
        String CITY_ID="CityId";
        String STATE="State";

        String STATE_Id="StateId";
        String Project_Name="ProjectName";
        String Project_Id="ProjectId";
        String Locality_Id="LocalityId";
        String Type="Type";
        String Address="Address";
        String BuilderId="BuilderId";
        String BuilderName="BuilderName";
        String WalkThroughVideoURL="WalkThroughVideoURL";
        String Offer="Offer";
        String AccName="AccName";
        String AccNumber="AccNumber";
        String BankName="BankName";
        String BranchName="BranchName";
        String BspRange="BspRange";
        String BuilderNameInUp="BuilderNameInUp";
        String Commercial="Commercial";
        String CommisionRemarks="CommisionRemarks";
        String ConnectivityScore="ConnectivityScore";
        String Country="Country";
        String CoverImage="CoverImage";
        String DeveloperIncentive="DeveloperIncentive";
        String EndDate="EndDate";
        String EndOfferDate="EndOfferDate";
        String Exclusive="Exclusive";
        String FDI="FDI";
        String GreenArea="GreenArea";
        String Hallipad="Hallipad";
        String Ifsc="Ifsc";
        String InternalIncentive="InternalIncentive";
        String LaunchDate="LaunchDate";
        String LifeStyleScore="LifeStyleScore";
        String LivabilityScore="LivabilityScore";
        String Locality="Locality";
        String LogoImageURL="LogoImageURL";
        String MetaTitle="MetaTitle";
        String MouURL="MouURL";
        String NewsUpdate="NewsUpdate";
        String NumberOfBuilderFloor="NumberOfBuilderFloor";
        String NumberofMultiStoryTowers="NumberofMultiStoryTowers";
        String NumberOfPlots="NumberOfPlots";
        String NumberOfShops="NumberOfShops";
        String NumberOfUnits="NumberOfUnits";
        String NumberOfVillas="NumberOfVillas";
        String OfferAvailable="OfferAvailable";
        String OfferLink="OfferLink";
        String OnBoardingDate="OnBoardingDate";
        String Phone="Phone";
        String PinCode="PinCode";
        String TargetAmount="TargetAmount";
        String PossessionDate="PossessionDate";
        String PreLaunchDate="PreLaunchDate";
        String PriceRange="PriceRange";
        String OtherImages="OtherImages";
        String ProjectArchitect="ProjectArchitect";
        String ProjectbrowcherPdfURL="ProjectbrowcherPdfURL";
        String ProjectSummary="ProjectSummary";
        String PropertyTypeJson="PropertyTypeJson";
        String PropertyTypeRange="PropertyTypeRange";
        String ReraId="ReraId";
        String RMAgentId="RMAgentId";
        String RMAgentName="RMAgentName";
        String RMAgentPhone="RMAgentPhone";
        String SafetyScore="SafetyScore";
        String SceoURL="SceoURL";
        String StartDate="StartDate";
        String sCoverImage="sCoverImage";
        String SearchNameInUp="SearchNameInUp";
        String SizeRange="SizeRange";
        String Sold="Sold";
        String StartOfferDate="StartOfferDate";
        String Tentative="Tentative";
        String TimeStamp="TimeStamp";
        String TotalArea="TotalArea";
        String TotalNumberBlock="TotalNumberBlock";
        String ValueForMoneyScore="ValueForMoneyScore";
        String VirtualTour="VirtualTour";
        String LocalityName="LocalityName";
        String Rank="Rank";
        String ID="Id";
        String Area_Unit="AreaUnit";
        String CreateTime="CreateTime";
        String MetaDescription="MetaDescription";
        String MetaKeyword="MetaKeyword";
        String SearchTitle="SearchTitle";
        String Seourl="Seourl";
        String Source="Source";
        String SourceUrl="SourceUrl";
        String ClickURL="ClickURL";
        String Description="Description";
        String Image="Image";
        String NewsTime="NewsTime";
        String Title="Title";
        String CategoryId="CategoryId";
        String CategoryName="CategoryName";
        String Content="Content";
        String CreateById="CreateById";
        String CreateByName="CreateByName";
        String Excerpt="Excerpt";
        String FbPostId="FbPostId";
        String PopularScore="PopularScore";
        String TitleImage="TitleImage";
        String STitleImage="STitleImage";
        String SubCategoryId="SubCategoryId";
        String SubCategoryName="SubCategoryName";
        String AgentList="AgentList";
        String NotifiCationId="NotificationId";
        String ClickUrl="ClickUrl";
        String EndTime="EndTime";
        String StartTime="StartTime";
        String Text="Text";
        String Time="Time";
        String Uid="Uid";
        String Url="Url";
        String WelcomeUrl="WelcomeUrl";

    }
    public interface AnaylticsClassName
    {
        String PickCity="CitySelectionActivity";
        String HomeScreen="Home";
        String SearchScreen="SearchScreen";
        String FilterScreen="FilterScreen";
        String HomeDecor="HomeDecorIdeas";
        String HomeDecorDetailScreen="HomeDecorIdeasDetail";
        String ShareActivity="Share";
        String ImageGalleryZoomViewActivity="ImageGallery";
        String NewsScreen="News";
        String BlogListScreen="BlogList";
        String BlogDetailScreen="BlogDetail";
        String MoreScreen="More";
        String LoginScreen="Login";
        String ForgotPasswordScreen="ForgotPassword";
        String ResetPasswordScreen="ResetPassword";
        String RELATIONSHIP_MANAGER="RELATIONSHIP MANAGER";
        String PROPERTY_ADVISOR="PROPERTY ADVISOR";
        String AboutUsScreen="AboutUs";
        String UsedLibraryActivityScreen="UsedLibrary";
        String PrivatePolicyActivity="PrivatePolicyActivity";
        String TermsAndConditionsActivity="TermsAndConditions";
        String AboutUsViewActivity="AboutUsView";
        String RegisterPage="Register";
        String OTPScreen="OTP";
        String NotificationScreen="Notifications";
        String ProjectDetailsScreen="ProjectDetails";
        String ProjectListScreen="ProjectListScreen";
        String NotificationsDetailsScreen="NotificationsDetails";
        String RecentScreen="RecentlyViewed";
    }

    public enum ApiName {
        getCityListAll,getFooterLink,getProjectByIds,getProjectByDistance,getProjectByCity,searchByName,getDecorList,getNewsList,login,forgotPassword,
        resetPasswordWithOTP,rateAgent,updateStat,resendOTPRequest,verifyPhoneNumber,verifyOTP,signUpUser,getUserNotificationList,getFavoriteProjectList,
        getProjectDetails
    }

    public enum EventClickName {
        SelectCityclick,SelectLocality,SelectDeveloper,SelectProject,SelectPopularSearch,SelectRecentSearch,SelectTopLocality,ClickProject,applyFilter,
        cancel,ok,resetFilter,ContactMePage_SubmitClicked,SaveSearch,homeDecoreItemclick,shareclick,videoPlay,newsItemClick,BlogClick,fbClick,TwitterClick,
        moreClick,contactSupport,LoginClick,logout,recentClick,notificationClick,blogClick,AboutUsClick,editImage,wishlistClick,saveSerachClick,
        siteVisitLayout,propertyLayoutIdea,profileClick,forgotPasswordClick,resendOtpclick,resetPasswordClick,RegistrationClick,callClick,submitClick,
        feedbackClick,termsAndConditionClick,privacyAndPolicy,rateClick,usedLibraryClick,notoficationClick,favouriteClick,exploreClick,finacialClick,
        paymentClick,requestpaymentClick
    }
}
