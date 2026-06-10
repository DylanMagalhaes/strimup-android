package com.strimup.feature.home.data

import com.strimup.feature.home.domain.StreamerRepository
import com.strimup.feature.home.domain.entity.StreamerEntity
import javax.inject.Inject

class FakeStreamerRepository @Inject constructor() : StreamerRepository {

    override suspend fun getRandomStreamers(): List<StreamerEntity> {
        return listOf(
            StreamerEntity(
                isLive = true,
                userName = "Squeezie",
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
                userName = "Gotaga",
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

    override suspend fun getInLiveStreamers(): List<StreamerEntity> {
        return listOf(
            StreamerEntity(
                isLive = true,
                userName = "Squeezie",
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
                userName = "Gotaga",
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
}