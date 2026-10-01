package com.strimup.feature.notification.domain.entity

sealed interface NotificationType {
    data class NewFavorite(val fanId: String?, val fanPseudo: String?) : NotificationType
    data object GlobalAnnouncement : NotificationType
    data object UgcMessage : NotificationType
    data object UgcOrderStatus : NotificationType
    data class Unknown(val rawType: String) : NotificationType
}
