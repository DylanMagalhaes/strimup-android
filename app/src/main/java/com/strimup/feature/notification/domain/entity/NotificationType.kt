package com.strimup.feature.notification.domain.entity

sealed interface NotificationType {
    data class NewFavorite(val fanId: String?, val fanPseudo: String?) : NotificationType
    data object GlobalAnnouncement : NotificationType
    data object UgcMessage : NotificationType
    data object UgcOrderStatus : NotificationType
    data class Unknown(val rawType: String) : NotificationType

    companion object {
        private const val NEW_FAVORITE = "new_favorite"
        private const val GLOBAL_ANNOUNCEMENT = "global_announcement"
        private const val UGC_MESSAGE = "ugc_message"
        private const val UGC_ORDER_STATUS = "ugc_order_status"

        fun fromApi(type: String, fanId: String? = null, fanPseudo: String? = null): NotificationType = when (type) {
            NEW_FAVORITE -> NewFavorite(fanId = fanId, fanPseudo = fanPseudo)
            GLOBAL_ANNOUNCEMENT -> GlobalAnnouncement
            UGC_MESSAGE -> UgcMessage
            UGC_ORDER_STATUS -> UgcOrderStatus
            else -> Unknown(rawType = type)
        }
    }
}
