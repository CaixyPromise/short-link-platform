declare namespace API {
  type AboutMeVO = {
    userAccount?: string;
    userName?: string;
    userPhone?: string;
    userEmail?: string;
    userGender?: number;
    userAvatar?: string;
  };

  type AddUserVO = {
    id?: number;
    userName?: string;
    userAccount?: string;
    userPassword?: string;
  };

  type ApiKeyVO = {
    accessKey?: string;
    secretKey?: string;
  };

  type CaptchaVO = {
    codeImage?: string;
    uuid?: string;
  };

  type checkFileExistParams = {
    scene: number;
    sha256: string;
    size: number;
  };

  type CheckFileExistResponse = {
    /** 上传token */
    token?: string;
    challenge?: FileHmacInfo;
  };

  type DeleteRequest = {
    id?: number;
  };

  type doActivateUserParams = {
    token: string;
    code: string;
  };

  type downloadFileByIdParams = {
    id: string;
    bizName: number;
  };

  type EncryptAccountVO = {
    phone?: string;
    email?: string;
  };

  type FileHmacInfo = {
    /** 一次性随机串，防重放 */
    nonce?: string;
    /** 挑战原文 */
    challenge?: string;
    /** 秒或毫秒级时间戳，用于过期校验 */
    timestamp?: number;
    /** 切片起始偏移 */
    offset?: number;
    /** 切片长度 */
    length?: number;
  };

  type forceLogoutParams = {
    userId: number;
  };

  type getGroupVOByIdParams = {
    gid: string;
  };

  type getLinkAccessLogsVOByIdParams = {
    id: number;
  };

  type getLinkBrowserStatsVOByIdParams = {
    id: number;
  };

  type getLinkDeviceStatsVOByIdParams = {
    id: number;
  };

  type getLinkGotoVOByIdParams = {
    id: number;
  };

  type getLinkLocaleStatsVOByIdParams = {
    id: number;
  };

  type getLinkNetworkStatsVOByIdParams = {
    id: number;
  };

  type getLinkOsStatsVOByIdParams = {
    id: number;
  };

  type getLinkStatsTodayVOByIdParams = {
    id: number;
  };

  type getLinkVOByIdParams = {
    id: number;
  };

  type getOnlineUsersParams = {
    page?: number;
    size?: number;
  };

  type getRegistrationInfoByParamsParams = {
    token: string;
  };

  type getUserByIdParams = {
    id: number;
  };

  type getUserVOByIdParams = {
    id: number;
  };

  type GithubGetAuthorizationUrlRequest = {
    redirectUri?: string;
    sessionId?: string;
  };

  type Group = {
    id?: number;
    createTime?: string;
    updateTime?: string;
    isDeleted?: number;
    gid?: string;
    name?: string;
    username?: string;
    description?: string;
    sortOrder?: number;
  };

  type GroupAddRequest = {
    groupName: string;
    description?: string;
  };

  type GroupDeleteRequest = {
    gid: string;
    newGroupId?: string;
  };

  type GroupEditRequest = {
    id?: number;
    title?: string;
    content?: string;
    tags?: string[];
  };

  type GroupItemVO = {
    gid?: string;
    name?: string;
    linkCount?: number;
    sortOrder?: number;
    createTime?: string;
  };

  type GroupQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    notId?: number;
    searchText?: string;
    title?: string;
    content?: string;
    tags?: string[];
    userId?: number;
  };

  type groupShortLinkStatsAccessRecordParams = {
    requestParam: ShortLinkGroupStatsAccessRecordReqDTO;
  };

  type groupShortLinkStatsParams = {
    requestParam: ShortLinkGroupStatsReqDTO;
  };

  type GroupUpdateInfoRequest = {
    gid: string;
    name?: string;
    description?: string;
  };

  type GroupUpdateOrderRequest = {
    gid: string;
    offset: number;
  };

  type GroupVO = {
    gid?: string;
    name?: string;
    sortOrder?: number;
    createTime?: string;
    description?: string;
  };

  type initOAuthLoginParams = {
    provider: string;
    authorizationUrlRequest: GithubGetAuthorizationUrlRequest;
  };

  type IPageShortLinkStatsAccessRecordRespDTO = {
    size?: number;
    current?: number;
    records?: ShortLinkStatsAccessRecordRespDTO[];
    total?: number;
    pages?: number;
  };

  type IPageUserFeedbackInfoVO = {
    size?: number;
    current?: number;
    records?: UserFeedbackInfoVO[];
    total?: number;
    pages?: number;
  };

  type LinkAccessLogs = {
    id?: number;
    createTime?: string;
    updateTime?: string;
    isDeleted?: number;
    fullShortUrl?: string;
    user?: string;
    ip?: string;
    browser?: string;
    os?: string;
    network?: string;
    device?: string;
    locale?: string;
  };

  type LinkAccessLogsAddRequest = {
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkAccessLogsEditRequest = {
    id?: number;
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkAccessLogsQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    notId?: number;
    searchText?: string;
    title?: string;
    content?: string;
    tags?: string[];
    userId?: number;
  };

  type LinkAccessLogsUpdateRequest = {
    id?: number;
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkAccessLogsVO = {
    id?: number;
    title?: string;
    content?: string;
    userId?: number;
    createTime?: string;
    updateTime?: string;
    tagList?: string[];
    user?: UserVO;
  };

  type LinkAddRequest = {
    originUrl: string;
    gid: string;
    linkName: string;
    validDateType: number;
    validDateStart?: string;
    validDateEnd?: string;
    describe?: string;
  };

  type LinkBrowserStats = {
    id?: number;
    createTime?: string;
    updateTime?: string;
    isDeleted?: number;
    fullShortUrl?: string;
    date?: string;
    cnt?: number;
    browser?: string;
  };

  type LinkBrowserStatsAddRequest = {
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkBrowserStatsEditRequest = {
    id?: number;
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkBrowserStatsQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    notId?: number;
    searchText?: string;
    title?: string;
    content?: string;
    tags?: string[];
    userId?: number;
  };

  type LinkBrowserStatsUpdateRequest = {
    id?: number;
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkBrowserStatsVO = {
    id?: number;
    title?: string;
    content?: string;
    userId?: number;
    createTime?: string;
    updateTime?: string;
    tagList?: string[];
    user?: UserVO;
  };

  type LinkCreateVO = {
    shortLink?: string;
    gid?: string;
    originUrl?: string;
  };

  type LinkDeviceStats = {
    id?: number;
    createTime?: string;
    updateTime?: string;
    isDeleted?: number;
    fullShortUrl?: string;
    date?: string;
    cnt?: number;
    device?: string;
  };

  type LinkDeviceStatsAddRequest = {
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkDeviceStatsEditRequest = {
    id?: number;
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkDeviceStatsQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    notId?: number;
    searchText?: string;
    title?: string;
    content?: string;
    tags?: string[];
    userId?: number;
  };

  type LinkDeviceStatsUpdateRequest = {
    id?: number;
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkDeviceStatsVO = {
    id?: number;
    title?: string;
    content?: string;
    userId?: number;
    createTime?: string;
    updateTime?: string;
    tagList?: string[];
    user?: UserVO;
  };

  type LinkEditRequest = {
    id?: number;
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkGoto = {
    id?: number;
    gid?: string;
    fullShortUrl?: string;
  };

  type LinkGotoAddRequest = {
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkGotoEditRequest = {
    id?: number;
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkGotoQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    notId?: number;
    searchText?: string;
    title?: string;
    content?: string;
    tags?: string[];
    userId?: number;
  };

  type LinkGotoUpdateRequest = {
    id?: number;
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkGotoVO = {
    id?: number;
    title?: string;
    content?: string;
    userId?: number;
    createTime?: string;
    updateTime?: string;
    tagList?: string[];
    user?: UserVO;
  };

  type LinkLocaleStats = {
    id?: number;
    createTime?: string;
    updateTime?: string;
    isDeleted?: number;
    fullShortUrl?: string;
    date?: string;
    cnt?: number;
    province?: string;
    city?: string;
    adcode?: string;
    country?: string;
  };

  type LinkLocaleStatsAddRequest = {
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkLocaleStatsEditRequest = {
    id?: number;
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkLocaleStatsQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    notId?: number;
    searchText?: string;
    title?: string;
    content?: string;
    tags?: string[];
    userId?: number;
  };

  type LinkLocaleStatsUpdateRequest = {
    id?: number;
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkLocaleStatsVO = {
    id?: number;
    title?: string;
    content?: string;
    userId?: number;
    createTime?: string;
    updateTime?: string;
    tagList?: string[];
    user?: UserVO;
  };

  type LinkNetworkStats = {
    id?: number;
    createTime?: string;
    updateTime?: string;
    isDeleted?: number;
    fullShortUrl?: string;
    date?: string;
    cnt?: number;
    network?: string;
  };

  type LinkNetworkStatsAddRequest = {
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkNetworkStatsEditRequest = {
    id?: number;
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkNetworkStatsQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    notId?: number;
    searchText?: string;
    title?: string;
    content?: string;
    tags?: string[];
    userId?: number;
  };

  type LinkNetworkStatsUpdateRequest = {
    id?: number;
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkNetworkStatsVO = {
    id?: number;
    title?: string;
    content?: string;
    userId?: number;
    createTime?: string;
    updateTime?: string;
    tagList?: string[];
    user?: UserVO;
  };

  type LinkOsStats = {
    id?: number;
    createTime?: string;
    updateTime?: string;
    isDeleted?: number;
    fullShortUrl?: string;
    date?: string;
    cnt?: number;
    os?: string;
  };

  type LinkOsStatsAddRequest = {
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkOsStatsEditRequest = {
    id?: number;
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkOsStatsQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    notId?: number;
    searchText?: string;
    title?: string;
    content?: string;
    tags?: string[];
    userId?: number;
  };

  type LinkOsStatsUpdateRequest = {
    id?: number;
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkOsStatsVO = {
    id?: number;
    title?: string;
    content?: string;
    userId?: number;
    createTime?: string;
    updateTime?: string;
    tagList?: string[];
    user?: UserVO;
  };

  type LinkQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    gid: string;
  };

  type LinkStatsToday = {
    id?: number;
    createTime?: string;
    updateTime?: string;
    isDeleted?: number;
    fullShortUrl?: string;
    date?: string;
    todayPv?: number;
    todayUv?: number;
    todayUip?: number;
  };

  type LinkStatsTodayAddRequest = {
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkStatsTodayEditRequest = {
    id?: number;
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkStatsTodayQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    notId?: number;
    searchText?: string;
    title?: string;
    content?: string;
    tags?: string[];
    userId?: number;
  };

  type LinkStatsTodayUpdateRequest = {
    id?: number;
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkStatsTodayVO = {
    id?: number;
    title?: string;
    content?: string;
    userId?: number;
    createTime?: string;
    updateTime?: string;
    tagList?: string[];
    user?: UserVO;
  };

  type LinkUpdateGroupRequest = {
    groupId?: string;
    newGroupId?: string;
    linkIds?: number[];
  };

  type LinkUpdateRequest = {
    id?: number;
    title?: string;
    content?: string;
    tags?: string[];
  };

  type LinkUpdateValidDateRequest = {
    validDateType: number;
    validDateStart?: string;
    validDateEnd?: string;
    groupId?: string;
    linkId?: number;
  };

  type LinkVO = {
    id?: number;
    domain?: string;
    linkName?: string;
    shortUri?: string;
    fullShortUrl?: string;
    originUrl?: string;
    clickNum?: number;
    gid?: string;
    favicon?: string;
    enableStatus?: number;
    createdType?: number;
    validDateType?: number;
    validDateStart?: string;
    validDateEnd?: string;
    description?: string;
    totalPv?: number;
    totalUv?: number;
    totalUip?: number;
  };

  type LoginUserVO = {
    userAccount?: string;
    userGender?: number;
    userEmail?: string;
    userName?: string;
    userAvatar?: string;
    userProfile?: string;
    userRole?: 'USER' | 'ADMIN' | 'BAN' | 'UNAUTHORIZED';
    token?: string;
  };

  type oAuthLoginCallbackParams = {
    provider: string;
    allParams: Record<string, any>;
  };

  type OrderItem = {
    column?: string;
    asc?: boolean;
  };

  type PageGroup = {
    records?: Group[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageGroup;
    searchCount?: PageGroup;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageGroupVO = {
    records?: GroupVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageGroupVO;
    searchCount?: PageGroupVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageLinkAccessLogs = {
    records?: LinkAccessLogs[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageLinkAccessLogs;
    searchCount?: PageLinkAccessLogs;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageLinkAccessLogsVO = {
    records?: LinkAccessLogsVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageLinkAccessLogsVO;
    searchCount?: PageLinkAccessLogsVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageLinkBrowserStats = {
    records?: LinkBrowserStats[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageLinkBrowserStats;
    searchCount?: PageLinkBrowserStats;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageLinkBrowserStatsVO = {
    records?: LinkBrowserStatsVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageLinkBrowserStatsVO;
    searchCount?: PageLinkBrowserStatsVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageLinkDeviceStats = {
    records?: LinkDeviceStats[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageLinkDeviceStats;
    searchCount?: PageLinkDeviceStats;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageLinkDeviceStatsVO = {
    records?: LinkDeviceStatsVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageLinkDeviceStatsVO;
    searchCount?: PageLinkDeviceStatsVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageLinkGoto = {
    records?: LinkGoto[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageLinkGoto;
    searchCount?: PageLinkGoto;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageLinkGotoVO = {
    records?: LinkGotoVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageLinkGotoVO;
    searchCount?: PageLinkGotoVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageLinkLocaleStats = {
    records?: LinkLocaleStats[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageLinkLocaleStats;
    searchCount?: PageLinkLocaleStats;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageLinkLocaleStatsVO = {
    records?: LinkLocaleStatsVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageLinkLocaleStatsVO;
    searchCount?: PageLinkLocaleStatsVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageLinkNetworkStats = {
    records?: LinkNetworkStats[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageLinkNetworkStats;
    searchCount?: PageLinkNetworkStats;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageLinkNetworkStatsVO = {
    records?: LinkNetworkStatsVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageLinkNetworkStatsVO;
    searchCount?: PageLinkNetworkStatsVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageLinkOsStats = {
    records?: LinkOsStats[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageLinkOsStats;
    searchCount?: PageLinkOsStats;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageLinkOsStatsVO = {
    records?: LinkOsStatsVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageLinkOsStatsVO;
    searchCount?: PageLinkOsStatsVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageLinkStatsToday = {
    records?: LinkStatsToday[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageLinkStatsToday;
    searchCount?: PageLinkStatsToday;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageLinkStatsTodayVO = {
    records?: LinkStatsTodayVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageLinkStatsTodayVO;
    searchCount?: PageLinkStatsTodayVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageLinkVO = {
    records?: LinkVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageLinkVO;
    searchCount?: PageLinkVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageMyFeedbackRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    title?: string;
  };

  type PageUser = {
    records?: User[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageUser;
    searchCount?: PageUser;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PageUserVO = {
    records?: UserVO[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageUserVO;
    searchCount?: PageUserVO;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    pages?: number;
  };

  type PostFeedbackRequest = {
    title: string;
    content: string;
    contactEmail: string;
    contactName: string;
  };

  type QueryApiKeyRequest = {
    publicKey: string;
  };

  type redirectUrlParams = {
    shortUri: string;
  };

  type RegistrationInfo = {
    email?: string;
    nickName?: string;
  };

  type ResultAboutMeVO = {
    code?: number;
    data?: AboutMeVO;
    message?: string;
  };

  type ResultAddUserVO = {
    code?: number;
    data?: AddUserVO;
    message?: string;
  };

  type ResultApiKeyVO = {
    code?: number;
    data?: ApiKeyVO;
    message?: string;
  };

  type ResultBoolean = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type ResultCaptchaVO = {
    code?: number;
    data?: CaptchaVO;
    message?: string;
  };

  type ResultCheckFileExistResponse = {
    code?: number;
    data?: CheckFileExistResponse;
    message?: string;
  };

  type ResultEncryptAccountVO = {
    code?: number;
    data?: EncryptAccountVO;
    message?: string;
  };

  type ResultGroupVO = {
    code?: number;
    data?: GroupVO;
    message?: string;
  };

  type ResultInteger = {
    code?: number;
    data?: number;
    message?: string;
  };

  type ResultIPageShortLinkStatsAccessRecordRespDTO = {
    code?: number;
    data?: IPageShortLinkStatsAccessRecordRespDTO;
    message?: string;
  };

  type ResultIPageUserFeedbackInfoVO = {
    code?: number;
    data?: IPageUserFeedbackInfoVO;
    message?: string;
  };

  type ResultLinkAccessLogsVO = {
    code?: number;
    data?: LinkAccessLogsVO;
    message?: string;
  };

  type ResultLinkBrowserStatsVO = {
    code?: number;
    data?: LinkBrowserStatsVO;
    message?: string;
  };

  type ResultLinkCreateVO = {
    code?: number;
    data?: LinkCreateVO;
    message?: string;
  };

  type ResultLinkDeviceStatsVO = {
    code?: number;
    data?: LinkDeviceStatsVO;
    message?: string;
  };

  type ResultLinkGotoVO = {
    code?: number;
    data?: LinkGotoVO;
    message?: string;
  };

  type ResultLinkLocaleStatsVO = {
    code?: number;
    data?: LinkLocaleStatsVO;
    message?: string;
  };

  type ResultLinkNetworkStatsVO = {
    code?: number;
    data?: LinkNetworkStatsVO;
    message?: string;
  };

  type ResultLinkOsStatsVO = {
    code?: number;
    data?: LinkOsStatsVO;
    message?: string;
  };

  type ResultLinkStatsTodayVO = {
    code?: number;
    data?: LinkStatsTodayVO;
    message?: string;
  };

  type ResultLinkVO = {
    code?: number;
    data?: LinkVO;
    message?: string;
  };

  type ResultListGroupItemVO = {
    code?: number;
    data?: GroupItemVO[];
    message?: string;
  };

  type ResultLoginUserVO = {
    code?: number;
    data?: LoginUserVO;
    message?: string;
  };

  type ResultLong = {
    code?: number;
    data?: number;
    message?: string;
  };

  type ResultPageGroup = {
    code?: number;
    data?: PageGroup;
    message?: string;
  };

  type ResultPageGroupVO = {
    code?: number;
    data?: PageGroupVO;
    message?: string;
  };

  type ResultPageLinkAccessLogs = {
    code?: number;
    data?: PageLinkAccessLogs;
    message?: string;
  };

  type ResultPageLinkAccessLogsVO = {
    code?: number;
    data?: PageLinkAccessLogsVO;
    message?: string;
  };

  type ResultPageLinkBrowserStats = {
    code?: number;
    data?: PageLinkBrowserStats;
    message?: string;
  };

  type ResultPageLinkBrowserStatsVO = {
    code?: number;
    data?: PageLinkBrowserStatsVO;
    message?: string;
  };

  type ResultPageLinkDeviceStats = {
    code?: number;
    data?: PageLinkDeviceStats;
    message?: string;
  };

  type ResultPageLinkDeviceStatsVO = {
    code?: number;
    data?: PageLinkDeviceStatsVO;
    message?: string;
  };

  type ResultPageLinkGoto = {
    code?: number;
    data?: PageLinkGoto;
    message?: string;
  };

  type ResultPageLinkGotoVO = {
    code?: number;
    data?: PageLinkGotoVO;
    message?: string;
  };

  type ResultPageLinkLocaleStats = {
    code?: number;
    data?: PageLinkLocaleStats;
    message?: string;
  };

  type ResultPageLinkLocaleStatsVO = {
    code?: number;
    data?: PageLinkLocaleStatsVO;
    message?: string;
  };

  type ResultPageLinkNetworkStats = {
    code?: number;
    data?: PageLinkNetworkStats;
    message?: string;
  };

  type ResultPageLinkNetworkStatsVO = {
    code?: number;
    data?: PageLinkNetworkStatsVO;
    message?: string;
  };

  type ResultPageLinkOsStats = {
    code?: number;
    data?: PageLinkOsStats;
    message?: string;
  };

  type ResultPageLinkOsStatsVO = {
    code?: number;
    data?: PageLinkOsStatsVO;
    message?: string;
  };

  type ResultPageLinkStatsToday = {
    code?: number;
    data?: PageLinkStatsToday;
    message?: string;
  };

  type ResultPageLinkStatsTodayVO = {
    code?: number;
    data?: PageLinkStatsTodayVO;
    message?: string;
  };

  type ResultPageLinkVO = {
    code?: number;
    data?: PageLinkVO;
    message?: string;
  };

  type ResultPageUser = {
    code?: number;
    data?: PageUser;
    message?: string;
  };

  type ResultPageUserVO = {
    code?: number;
    data?: PageUserVO;
    message?: string;
  };

  type ResultRegistrationInfo = {
    code?: number;
    data?: RegistrationInfo;
    message?: string;
  };

  type ResultShortLinkStatsRespDTO = {
    code?: number;
    data?: ShortLinkStatsRespDTO;
    message?: string;
  };

  type ResultString = {
    code?: number;
    data?: string;
    message?: string;
  };

  type ResultUser = {
    code?: number;
    data?: User;
    message?: string;
  };

  type ResultUserVO = {
    code?: number;
    data?: UserVO;
    message?: string;
  };

  type ShortLinkGroupStatsAccessRecordReqDTO = {
    records?: LinkAccessLogs[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageLinkAccessLogs;
    searchCount?: PageLinkAccessLogs;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    gid?: string;
    startDate?: string;
    endDate?: string;
    pages?: number;
  };

  type ShortLinkGroupStatsReqDTO = {
    gid?: string;
    startDate?: string;
    endDate?: string;
  };

  type ShortLinkStatsAccessDailyRespDTO = {
    date?: string;
    pv?: number;
    uv?: number;
    uip?: number;
  };

  type shortLinkStatsAccessRecordParams = {
    requestParam: ShortLinkStatsAccessRecordReqDTO;
  };

  type ShortLinkStatsAccessRecordReqDTO = {
    records?: LinkAccessLogs[];
    total?: number;
    size?: number;
    current?: number;
    orders?: OrderItem[];
    optimizeCountSql?: PageLinkAccessLogs;
    searchCount?: PageLinkAccessLogs;
    optimizeJoinOfCountSql?: boolean;
    maxLimit?: number;
    countId?: string;
    fullShortUrl?: string;
    gid?: string;
    startDate?: string;
    endDate?: string;
    enableStatus?: number;
    pages?: number;
  };

  type ShortLinkStatsAccessRecordRespDTO = {
    uvType?: string;
    browser?: string;
    os?: string;
    ip?: string;
    network?: string;
    device?: string;
    locale?: string;
    user?: string;
    createTime?: string;
  };

  type ShortLinkStatsBrowserRespDTO = {
    cnt?: number;
    browser?: string;
    ratio?: number;
  };

  type ShortLinkStatsDeviceRespDTO = {
    cnt?: number;
    device?: string;
    ratio?: number;
  };

  type ShortLinkStatsLocaleCNRespDTO = {
    cnt?: number;
    locale?: string;
    ratio?: number;
  };

  type ShortLinkStatsNetworkRespDTO = {
    cnt?: number;
    network?: string;
    ratio?: number;
  };

  type ShortLinkStatsOsRespDTO = {
    cnt?: number;
    os?: string;
    ratio?: number;
  };

  type shortLinkStatsParams = {
    requestParam: ShortLinkStatsReqDTO;
  };

  type ShortLinkStatsReqDTO = {
    fullShortUrl?: string;
    gid?: string;
    startDate?: string;
    endDate?: string;
    enableStatus?: number;
  };

  type ShortLinkStatsRespDTO = {
    pv?: number;
    uv?: number;
    uip?: number;
    daily?: ShortLinkStatsAccessDailyRespDTO[];
    localeCnStats?: ShortLinkStatsLocaleCNRespDTO[];
    hourStats?: number[];
    topIpStats?: ShortLinkStatsTopIpRespDTO[];
    weekdayStats?: number[];
    browserStats?: ShortLinkStatsBrowserRespDTO[];
    osStats?: ShortLinkStatsOsRespDTO[];
    uvTypeStats?: ShortLinkStatsUvRespDTO[];
    deviceStats?: ShortLinkStatsDeviceRespDTO[];
    networkStats?: ShortLinkStatsNetworkRespDTO[];
  };

  type ShortLinkStatsTopIpRespDTO = {
    cnt?: number;
    ip?: string;
  };

  type ShortLinkStatsUvRespDTO = {
    cnt?: number;
    uvType?: string;
    ratio?: number;
  };

  type updateLinkStatusParams = {
    groupId: string;
    linkId: number;
    status: number;
  };

  type UploadFileRequest = {
    biz: '0';
    token: string;
    fileName: string;
    /** 文件挑战签名 */
    signature?: string;
    /** 防重放字段 */
    nonce?: string;
    /** 时间戳 */
    timestamp?: number;
  };

  type User = {
    id?: number;
    createTime?: string;
    updateTime?: string;
    isDeleted?: number;
    userName?: string;
    nickName?: string;
    userPassword?: string;
    userPhone?: string;
    userEmail?: string;
    userGender?: number;
    userAvatar?: string;
    userProfile?: string;
    userRole?: string;
    deletionTime?: string;
  };

  type UserActivationRequest = {
    password?: string;
    confirmPassword?: string;
  };

  type UserAddRequest = {
    userAccount?: string;
    userAvatar?: string;
    userRole?: string;
  };

  type UserFeedbackInfoVO = {
    title?: string;
    contactEmail?: string;
    contactName?: string;
    isSolve?: number;
    replayTime?: string;
  };

  type userLoginByWxOpenParams = {
    code: string;
  };

  type UserLoginRequest = {
    userAccount?: string;
    captcha?: string;
    captchaId?: string;
    userPassword?: string;
  };

  type UserModifyPasswordRequest = {
    captchaCode: string;
    newPassword?: string;
    confirmPassword?: string;
  };

  type UserQueryRequest = {
    current?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    id?: number;
    unionId?: string;
    mpOpenId?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserRegisterRequest = {
    userName?: string;
    userEmail: string;
    captcha: string;
    captchaId: string;
  };

  type UserResetEmailRequest = {
    originalEmail?: string;
    newEmail?: string;
    code: string;
    password: string;
    token?: string;
  };

  type UserUpdateProfileRequest = {
    userName?: string;
    userGender?: number;
    userProfile?: string;
  };

  type UserUpdateRequest = {
    id?: number;
    userName?: string;
    userAvatar?: string;
    userProfile?: string;
    userRole?: string;
    userGender?: number;
  };

  type UserVO = {
    id?: number;
    userName?: string;
    nickName?: string;
    unionId?: string;
    githubId?: number;
    githubUserName?: string;
    userPhone?: string;
    userEmail?: string;
    mpOpenId?: string;
    userGender?: number;
    userAvatar?: string;
    userProfile?: string;
    userRole?: 'USER' | 'ADMIN' | 'BAN' | 'UNAUTHORIZED';
    createTime?: string;
    updateTime?: string;
    isDelete?: number;
    loginIp?: string;
    loginLocation?: string;
    browser?: string;
    os?: string;
    loginTime?: number;
    expireTime?: number;
    token?: string;
    sessionId?: string;
  };
}
