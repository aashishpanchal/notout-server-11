package com.choic11.model.customer;

import com.choic11.model.TblCountry;
import com.choic11.model.TblState;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the tbl_customers database table.
 */
@Entity
@Table(name = "tbl_customers")
@NamedQuery(name = "TblCustomer.findAll", query = "SELECT t FROM TblCustomer t")
public class TblCustomer implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Formula(value = "concat(country_mobile_code,phone)")
    private String fullPhone;

    private String addressline1;

    private String addressline2;

    @Column(name = "affiliate_percent")
    private float affiliatePercent;

    @Column(name = "bonus_wallet")
    private float bonusWallet;

    @Column(name = "pending_wid_amount")
    private float pendingWidWallet;

    private String city;

    Integer country;

    @Column(name = "country_mobile_code")
    private String countryMobileCode;

    private BigInteger created;

    @Column(name = "created_by")
    private int createdBy;

    @Column(name = "deposit_wallet")
    private float depositWallet;

    private BigInteger dob;

    @Column(name = "dynamic_link")
    private String dynamicLink;

    private String email;

    @Column(name = "email_token")
    private String emailToken;

    @Column(name = "email_token_at")
    private BigInteger emailTokenAt;

    @Lob
    @Column(name = "external_image")
    private String externalImage;

    private String firstname;

    @Column(name = "follower_count")
    private int followerCount;

    @Column(name = "following_count")
    private int followingCount;

    private String image;

    @Column(name = "is_admin")
    private String isAdmin;

    @Column(name = "is_affiliate")
    private String isAffiliate;

    @Column(name = "is_deleted")
    private String isDeleted;

    @Column(name = "is_email_verified")
    private String isEmailVerified = "N";

    @Column(name = "is_fake")
    private String isFake;

    @Column(name = "is_phone_verified")

    private String isPhoneVerified = "N";

    @Column(name = "is_social")
    private String isSocial;

    private String lastname;

    private BigInteger modified;

    @Column(name = "modified_by")
    private BigInteger modifiedBy;

    @Column(name = "noti_seen_time")
    private BigInteger notiSeenTime;

    private String password;

    private String phone;

    @Column(name = "paytmphone")
    private String paytmphone;

    private String pincode;

    @Column(name = "post_count")
    private int postCount;

    @Column(name = "referral_code")
    private String referralCode;

    @Column(name = "repeat_count")
    private int repeatCount;

    @Column(name = "rz_contact_id")
    private String rzContactId;

    @Column(name = "rz_fund_account_id")
    private String rzFundAccountId;

    private String slug;

    @Column(name = "social_id")
    private String socialId;

    @Column(name = "social_type")

    private String socialType = "";

    private String status;

    @Column(name = "team_change")
    private String teamChange;

    @Column(name = "team_name")
    private String teamName;

    @Column(name = "used_referral_code")
    private String usedReferralCode;

    @Column(name = "used_referral_user_id")
    private int usedReferralUserId;

    @Column(name = "used_refferal_amount")
    private float usedRefferalAmount;

    @Column(name = "used_refferal_amount_deposit")
    private float usedRefferalAmountDeposit;

    @Column(name = "used_referral_given_amount")
    private float usedRefferalGivenAmount;

    @Column(name = "used_referral_given_amount_deposit")
    private float usedRefferalGivenAmountDeposit;

    @Column(name = "winning_wallet")
    private float winningWallet;

    @Column(name = "paincard_id")
    private int paincardId;

    @Column(name = "bankdetail_id")
    private int bankdetailId;

    @Column(name = "wrong_attemp_create")
    private BigInteger wrongAttempCreate;

    Integer state;

    @ManyToOne
    @JoinColumn(name = "state", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private TblState tblState;

    @OneToOne
    @JoinColumn(name = "bankdetail_id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private TblCustomerBankdetail tblCustomerBankdetail;

    @OneToOne
    @JoinColumn(name = "paincard_id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private TblCustomerPaincard tblCustomerPaincard;

    @ManyToOne
    @JoinColumn(name = "country", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private TblCountry tblCountry;

    @Transient
    private Integer customerLoginId;

    public TblCustomer() {
    }

    public float getUsedRefferalGivenAmountDeposit() {
        return usedRefferalGivenAmountDeposit;
    }

    public void setUsedRefferalGivenAmountDeposit(float usedRefferalGivenAmountDeposit) {
        this.usedRefferalGivenAmountDeposit = usedRefferalGivenAmountDeposit;
    }

    public TblCustomer(int customer_id) {
        this.id = customer_id;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullPhone() {
        return fullPhone;
    }

    public void setFullPhone(String fullPhone) {
        this.fullPhone = fullPhone;
    }

    public String getAddressline1() {
        return this.addressline1;
    }

    public void setAddressline1(String addressline1) {
        this.addressline1 = addressline1;
    }

    public String getAddressline2() {
        return this.addressline2;
    }

    public void setAddressline2(String addressline2) {
        this.addressline2 = addressline2;
    }

    public float getAffiliatePercent() {
        return this.affiliatePercent;
    }

    public void setAffiliatePercent(float affiliatePercent) {
        this.affiliatePercent = affiliatePercent;
    }

    public float getBonusWallet() {
        return this.bonusWallet;
    }

    public void setBonusWallet(float bonusWallet) {
        this.bonusWallet = bonusWallet;
    }

    public float getPendingWidWallet() {
        return pendingWidWallet;
    }

    public void setPendingWidWallet(float pendingWidWallet) {
        this.pendingWidWallet = pendingWidWallet;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getCountry() {
        return country;
    }

    public void setCountry(Integer country) {
        this.country = country;
    }

    public TblCountry getTblCountry() {
        return this.tblCountry;
    }

    public void setTblCountry(TblCountry tblCountry) {
        this.tblCountry = tblCountry;
    }

    public String getCountryMobileCode() {
        return this.countryMobileCode;
    }

    public void setCountryMobileCode(String countryMobileCode) {
        this.countryMobileCode = countryMobileCode;
    }

    public BigInteger getCreated() {
        return this.created;
    }

    public void setCreated(BigInteger created) {
        this.created = created;
    }

    public int getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public float getDepositWallet() {
        return this.depositWallet;
    }

    public void setDepositWallet(float depositWallet) {
        this.depositWallet = depositWallet;
    }

    public BigInteger getDob() {
        return this.dob;
    }

    public void setDob(BigInteger dob) {
        this.dob = dob;
    }

    public String getDynamicLink() {
        return this.dynamicLink;
    }

    public void setDynamicLink(String dynamicLink) {
        this.dynamicLink = dynamicLink;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailToken() {
        return this.emailToken;
    }

    public void setEmailToken(String emailToken) {
        this.emailToken = emailToken;
    }

    public BigInteger getEmailTokenAt() {
        return this.emailTokenAt;
    }

    public void setEmailTokenAt(BigInteger emailTokenAt) {
        this.emailTokenAt = emailTokenAt;
    }

    public String getExternalImage() {
        return this.externalImage;
    }

    public void setExternalImage(String externalImage) {
        this.externalImage = externalImage;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public int getFollowerCount() {
        return this.followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public int getFollowingCount() {
        return this.followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIsAdmin() {
        return this.isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getIsAffiliate() {
        return this.isAffiliate;
    }

    public void setIsAffiliate(String isAffiliate) {
        this.isAffiliate = isAffiliate;
    }

    public String getIsDeleted() {
        return this.isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getIsEmailVerified() {
        return this.isEmailVerified;
    }

    public void setIsEmailVerified(String isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    public String getIsFake() {
        return this.isFake;
    }

    public void setIsFake(String isFake) {
        this.isFake = isFake;
    }

    public String getIsPhoneVerified() {
        return this.isPhoneVerified;
    }

    public void setIsPhoneVerified(String isPhoneVerified) {
        this.isPhoneVerified = isPhoneVerified;
    }

    public String getIsSocial() {
        return this.isSocial;
    }

    public void setIsSocial(String isSocial) {
        this.isSocial = isSocial;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public BigInteger getModified() {
        return this.modified;
    }

    public void setModified(BigInteger modified) {
        this.modified = modified;
    }

    public BigInteger getModifiedBy() {
        return this.modifiedBy;
    }

    public void setModifiedBy(BigInteger modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public BigInteger getNotiSeenTime() {
        return this.notiSeenTime;
    }

    public void setNotiSeenTime(BigInteger notiSeenTime) {
        this.notiSeenTime = notiSeenTime;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPaytmphone() {
        return paytmphone;
    }

    public void setPaytmphone(String paytmphone) {
        this.paytmphone = paytmphone;
    }


    public String getPincode() {
        return this.pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public int getPostCount() {
        return this.postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public String getReferralCode() {
        return this.referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public int getRepeatCount() {
        return this.repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public String getRzContactId() {
        return this.rzContactId;
    }

    public void setRzContactId(String rzContactId) {
        this.rzContactId = rzContactId;
    }

    public String getRzFundAccountId() {
        return this.rzFundAccountId;
    }

    public void setRzFundAccountId(String rzFundAccountId) {
        this.rzFundAccountId = rzFundAccountId;
    }

    public String getSlug() {
        return this.slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSocialId() {
        return this.socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public String getSocialType() {
        return this.socialType;
    }

    public void setSocialType(String socialType) {
        this.socialType = socialType;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTeamChange() {
        return this.teamChange;
    }

    public void setTeamChange(String teamChange) {
        this.teamChange = teamChange;
    }

    public String getTeamName() {
        return this.teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getUsedReferralCode() {
        return this.usedReferralCode;
    }

    public void setUsedReferralCode(String usedReferralCode) {
        this.usedReferralCode = usedReferralCode;
    }

    public int getUsedReferralUserId() {
        return this.usedReferralUserId;
    }

    public void setUsedReferralUserId(int usedReferralUserId) {
        this.usedReferralUserId = usedReferralUserId;
    }

    public float getUsedRefferalAmount() {
        return this.usedRefferalAmount;
    }

    public void setUsedRefferalAmount(float usedRefferalAmount) {
        this.usedRefferalAmount = usedRefferalAmount;
    }

    public float getUsedRefferalAmountDeposit() {
        return usedRefferalAmountDeposit;
    }

    public void setUsedRefferalAmountDeposit(float usedRefferalAmountDeposit) {
        this.usedRefferalAmountDeposit = usedRefferalAmountDeposit;
    }

    public float getUsedRefferalGivenAmount() {
        return usedRefferalGivenAmount;
    }

    public void setUsedRefferalGivenAmount(float usedRefferalGivenAmount) {
        this.usedRefferalGivenAmount = usedRefferalGivenAmount;
    }

    public float getWinningWallet() {
        return this.winningWallet;
    }

    public void setWinningWallet(float winningWallet) {
        this.winningWallet = winningWallet;
    }

    public int getPaincardId() {
        return paincardId;
    }

    public void setPaincardId(int paincardId) {
        this.paincardId = paincardId;
    }

    public int getBankdetailId() {
        return bankdetailId;
    }

    public void setBankdetailId(int bankdetailId) {
        this.bankdetailId = bankdetailId;
    }

    public BigInteger getWrongAttempCreate() {
        return this.wrongAttempCreate;
    }

    public void setWrongAttempCreate(BigInteger wrongAttempCreate) {
        this.wrongAttempCreate = wrongAttempCreate;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public TblState getTblState() {
        return this.tblState;
    }

    public void setTblState(TblState tblState) {
        this.tblState = tblState;
    }

    public TblCustomerBankdetail getTblCustomerBankdetail() {
        return this.tblCustomerBankdetail;
    }

    public void setTblCustomerBankdetail(TblCustomerBankdetail tblCustomerBankdetail) {
        this.tblCustomerBankdetail = tblCustomerBankdetail;
    }

    public TblCustomerPaincard getTblCustomerPaincard() {
        return this.tblCustomerPaincard;
    }

    public void setTblCustomerPaincard(TblCustomerPaincard tblCustomerPaincard) {
        this.tblCustomerPaincard = tblCustomerPaincard;
    }

    public boolean isDeactive() {
        return getStatus().equals("D");
    }

    public static TblCustomer getInstance() {
        TblCustomer tblCustomer = new TblCustomer();

        tblCustomer.setIsSocial("N");
        tblCustomer.setSocialType("");
        tblCustomer.setDob(BigInteger.valueOf(0));
        tblCustomer.setCountry(0);
        tblCustomer.setState(0);
        tblCustomer.setStatus("A");
        tblCustomer.setIsPhoneVerified("N");
        tblCustomer.setIsEmailVerified("N");
        tblCustomer.setIsDeleted("N");
        tblCustomer.setUsedReferralCode("");
        tblCustomer.setUsedReferralUserId(0);
        tblCustomer.setUsedRefferalAmount(0f);
        tblCustomer.setUsedRefferalAmountDeposit(0f);
        tblCustomer.setUsedRefferalGivenAmount(0f);
        tblCustomer.setUsedRefferalGivenAmountDeposit(0f);
        tblCustomer.setBonusWallet(0f);
        tblCustomer.setDepositWallet(0f);
        tblCustomer.setWinningWallet(0f);
        tblCustomer.setPendingWidWallet(0f);
        tblCustomer.setFollowerCount(0);
        tblCustomer.setFollowingCount(0);
        tblCustomer.setPostCount(0);
        tblCustomer.setTeamChange("N");
        tblCustomer.setPaincardId(0);
        tblCustomer.setBankdetailId(0);
        tblCustomer.setEmailTokenAt(BigInteger.valueOf(0));
        tblCustomer.setNotiSeenTime(BigInteger.valueOf(0));
        tblCustomer.setCreatedBy(0);
        tblCustomer.setIsAdmin("0");
        tblCustomer.setIsFake("0");
        tblCustomer.setIsAffiliate("0");
        tblCustomer.setAffiliatePercent(0f);
        tblCustomer.setWrongAttempCreate(BigInteger.valueOf(0));

        return tblCustomer;
    }

    public boolean isAdminCustomer() {
        return isAdmin != null && isAdmin.equals("1");
    }

    public boolean isFakeCustomer() {
        return isFake != null && isFake.equals("1");
    }

    public boolean isAffiliateCustomer() {
        return isAffiliate != null && isAffiliate.equals("1");
    }


    public Integer getCustomerLoginId() {
        return customerLoginId;
    }

    public void setCustomerLoginId(Integer customerLoginId) {
        this.customerLoginId = customerLoginId;
    }

    public boolean isValidCustomerLoginId(){
        return getCustomerLoginId()!=null && getCustomerLoginId()>0;
    }
}