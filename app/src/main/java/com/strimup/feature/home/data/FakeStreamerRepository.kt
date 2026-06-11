package com.strimup.feature.home.data

import com.strimup.feature.home.domain.StreamerRepository
import com.strimup.feature.home.domain.entity.StreamerEntity
import javax.inject.Inject

class FakeStreamerRepository @Inject constructor() : StreamerRepository {

    override suspend fun getRandomStreamers(favoriteStreamerIds: List<String>): List<StreamerEntity> {
        return listOf(
            StreamerEntity(
                isLive = true,
                liveTitle = "lore ipsum dolores",
                userName = "Squeezie",
                isFavorite = true,
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTMW1ej_GeDal7F5wF9obJX-34O1-_9-VqpDk_o_NAnyYVUHFbGH-ayQ25e6j549GpqNTlWLOcOWUtqkExn3pmptkpPdMfZnp0TQ4lLsw&s=10",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/squeezie",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.youtube.com/@Squeezie",
                        StreamerEntity.Social.Type.Youtube
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/xsqueezie",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            ),

            StreamerEntity(
                isLive = false,
                liveTitle = null,
                userName = "Gotaga",
                isFavorite = false,
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQwZvRGAD3H9tbtJLc5dd-Y4uxt2CbAWxZs60ZrSQWAbg8JrBHecFaiwbr6qJeS6pwpIA-1VIwuZ_4VWdQ_DB3siF4ugHiXf2NriIPeHes&s=10",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/gotaga",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.youtube.com/@Gotaga",
                        StreamerEntity.Social.Type.Youtube
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/gotagacorp",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            ),
        )
    }

    override suspend fun getInLiveStreamers(favoriteStreamerIds: List<String>): List<StreamerEntity> {
        return listOf(
            StreamerEntity(
                isLive = true,
                liveTitle = "lore ipsum dolores",
                userName = "Squeezie",
                isFavorite = true,
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTMW1ej_GeDal7F5wF9obJX-34O1-_9-VqpDk_o_NAnyYVUHFbGH-ayQ25e6j549GpqNTlWLOcOWUtqkExn3pmptkpPdMfZnp0TQ4lLsw&s=10",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/squeezie",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.youtube.com/@Squeezie",
                        StreamerEntity.Social.Type.Youtube
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/xsqueezie",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            ),

            StreamerEntity(
                isLive = true,
                liveTitle = "lore ipsum dolores",
                userName = "Gotaga",
                isFavorite = true,
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQwZvRGAD3H9tbtJLc5dd-Y4uxt2CbAWxZs60ZrSQWAbg8JrBHecFaiwbr6qJeS6pwpIA-1VIwuZ_4VWdQ_DB3siF4ugHiXf2NriIPeHes&s=10",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/gotaga",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.youtube.com/@Gotaga",
                        StreamerEntity.Social.Type.Youtube
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/gotagacorp",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            ),
        )
    }

    override suspend fun getFavoriteStreamerIds(): List<String> {
        throw NotImplementedError()
    }
}