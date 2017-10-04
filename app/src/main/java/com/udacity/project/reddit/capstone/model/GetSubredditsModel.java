package com.udacity.project.reddit.capstone.model;

import com.google.common.primitives.Booleans;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Neha on 10-08-2017.
 */

public class GetSubredditsModel {

    @SerializedName("kind")
    @Expose
    public String kind;
    @SerializedName("data")
    @Expose
    public Data data;


    public class Data {

        @SerializedName("modhash")
        @Expose
        public String modhash;

        @SerializedName("children")
        @Expose
        public List<Child> children = null;
        @SerializedName("after")
        @Expose
        public String after;
        @SerializedName("before")
        @Expose
        public Object before;

    }

    public static List<Data_> subredditDataList = new ArrayList<>();

    public class Data_ {

        @SerializedName("banner_img")
        @Expose
        public String bannerImg;
        @SerializedName("user_sr_theme_enabled")
        @Expose
        public Boolean userSrThemeEnabled;
        @SerializedName("user_flair_text")
        @Expose
        public Object userFlairText;
        @SerializedName("submit_text_html")
        @Expose
        public String submitTextHtml;
        @SerializedName("user_is_banned")
        @Expose
        public Object userIsBanned;
        @SerializedName("wiki_enabled")
        @Expose
        public Boolean wikiEnabled;
        @SerializedName("show_media")
        @Expose
        public Boolean showMedia;
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("display_name_prefixed")
        @Expose
        public String displayNamePrefixed;
        @SerializedName("submit_text")
        @Expose
        public String submitText;
        @SerializedName("user_can_flair_in_sr")
        @Expose
        public Object userCanFlairInSr;
        @SerializedName("display_name")
        @Expose
        public String displayName;
        @SerializedName("header_img")
        @Expose
        public String headerImg;
        @SerializedName("description_html")
        @Expose
        public String descriptionHtml;
        @SerializedName("title")
        @Expose
        public String title;
        @SerializedName("collapse_deleted_comments")
        @Expose
        public Boolean collapseDeletedComments;
        @SerializedName("user_has_favorited")
        @Expose
        public Object userHasFavorited;
        @SerializedName("over18")
        @Expose
        public Boolean over18;
        @SerializedName("public_description_html")
        @Expose
        public String publicDescriptionHtml;
        @SerializedName("spoilers_enabled")
        @Expose
        public Boolean spoilersEnabled;
        @SerializedName("icon_size")
        @Expose
        public List<Integer> iconSize = null;
        @SerializedName("audience_target")
        @Expose
        public String audienceTarget;
        @SerializedName("suggested_comment_sort")
        @Expose
        public Object suggestedCommentSort;
        @SerializedName("active_user_count")
        @Expose
        public Object activeUserCount;
        @SerializedName("icon_img")
        @Expose
        public String iconImg;
        @SerializedName("header_title")
        @Expose
        public String headerTitle;
        @SerializedName("user_is_muted")
        @Expose
        public Object userIsMuted;
        @SerializedName("submit_link_label")
        @Expose
        public Object submitLinkLabel;
        @SerializedName("accounts_active")
        @Expose
        public Object accountsActive;
        @SerializedName("public_traffic")
        @Expose
        public Boolean publicTraffic;
        @SerializedName("header_size")
        @Expose
        public List<Integer> headerSize = null;
        @SerializedName("subscribers")
        @Expose
        public Integer subscribers;
        @SerializedName("user_flair_css_class")
        @Expose
        public Object userFlairCssClass;
        @SerializedName("submit_text_label")
        @Expose
        public String submitTextLabel;
        @SerializedName("whitelist_status")
        @Expose
        public String whitelistStatus;
        @SerializedName("user_sr_flair_enabled")
        @Expose
        public Object userSrFlairEnabled;
        @SerializedName("lang")
        @Expose
        public String lang;
        @SerializedName("user_is_moderator")
        @Expose
        public Object userIsModerator;
        @SerializedName("key_color")
        @Expose
        public String keyColor;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("user_flair_enabled_in_sr")
        @Expose
        public Boolean userFlairEnabledInSr;
        @SerializedName("created")
        @Expose
        public Integer created;
        @SerializedName("url")
        @Expose
        public String url;
        @SerializedName("quarantine")
        @Expose
        public Boolean quarantine;
        @SerializedName("hide_ads")
        @Expose
        public Boolean hideAds;
        @SerializedName("created_utc")
        @Expose
        public Integer createdUtc;
        @SerializedName("banner_size")
        @Expose
        public List<Integer> bannerSize = null;
        @SerializedName("user_is_contributor")
        @Expose
        public Object userIsContributor;
        @SerializedName("accounts_active_is_fuzzed")
        @Expose
        public Boolean accountsActiveIsFuzzed;
        @SerializedName("advertiser_category")
        @Expose
        public String advertiserCategory;
        @SerializedName("public_description")
        @Expose
        public String publicDescription;
        @SerializedName("allow_images")
        @Expose
        public Boolean allowImages;
        @SerializedName("show_media_preview")
        @Expose
        public Boolean showMediaPreview;
        @SerializedName("comment_score_hide_mins")
        @Expose
        public Integer commentScoreHideMins;
        @SerializedName("subreddit_type")
        @Expose
        public String subredditType;
        @SerializedName("submission_type")
        @Expose
        public String submissionType;
        @SerializedName("user_is_subscriber")
        @Expose
        public boolean userIsSubscriber;

    }

    public class Kind {

        @SerializedName("kind")
        @Expose
        public String kind;
        @SerializedName("data")
        @Expose
        public Data data;

    }

    public class Child {

        @SerializedName("kind")
        @Expose
        public String kind;
        @SerializedName("data")
        @Expose
        public Data_ data;


    }


}

