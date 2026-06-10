package com.strimup.feature.home.data

import com.strimup.feature.home.domain.StreamerRepository
import com.strimup.feature.home.domain.entity.StreamerEntity
import javax.inject.Inject

class FakeStreamerRepository @Inject constructor() : StreamerRepository {

    override suspend fun getRandomStreamers(): List<StreamerEntity> {
        return listOf(
            StreamerEntity(
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

            StreamerEntity(
                userName = "Kameto",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_kameto_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/kamet0",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/kametol0l",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            ),

            StreamerEntity(
                userName = "AmineMatue",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_amine_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/aminematue",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.tiktok.com/@aminematue",
                        StreamerEntity.Social.Type.Tiktok
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/aminematue",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            ),

            StreamerEntity(
                userName = "MisterV",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_misterv_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.youtube.com/@misterv",
                        StreamerEntity.Social.Type.Youtube
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/yvick",
                        StreamerEntity.Social.Type.Instagram
                    ),
                    StreamerEntity.Social(
                        "https://www.tiktok.com/@misterv",
                        StreamerEntity.Social.Type.Tiktok
                    )
                )
            ),

            StreamerEntity(
                userName = "Maghla",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_maghla_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/maghla",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/maghlarnaud",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            ),

            StreamerEntity(
                userName = "Inoxtag",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_inoxtag_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.youtube.com/@Inoxtag",
                        StreamerEntity.Social.Type.Youtube
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/inoxtag",
                        StreamerEntity.Social.Type.Instagram
                    ),
                    StreamerEntity.Social(
                        "https://www.tiktok.com/@inoxtag",
                        StreamerEntity.Social.Type.Tiktok
                    )
                )
            ),

            StreamerEntity(
                userName = "Michou",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_michou_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/michou",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.youtube.com/@Michou",
                        StreamerEntity.Social.Type.Youtube
                    ),
                    StreamerEntity.Social(
                        "https://www.tiktok.com/@michou_yt",
                        StreamerEntity.Social.Type.Tiktok
                    )
                )
            ),

            StreamerEntity(
                userName = "BagheraJones",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_baghera_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/bagherajones",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/bagherajones",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            ),

            StreamerEntity(
                userName = "Etoiles",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_etoiles_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/etoiles",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.youtube.com/@EtoilesTwitch", StreamerEntity.Social.Type.Youtube
                    )
                )
            ),

            StreamerEntity(
                userName = "Domingo",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_domingo_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/domingo",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.youtube.com/@DomingoReplay", StreamerEntity.Social.Type.Youtube
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/paolodomingo",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            ),

            StreamerEntity(
                userName = "Jury (J大きい)",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_tiktok_creator_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.tiktok.com/@khaby.lame",
                        StreamerEntity.Social.Type.Tiktok
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/khaby00",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            ),

            StreamerEntity(
                userName = "Grimkujow",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_grim_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/grimkujow",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.tiktok.com/@grimkujow_", StreamerEntity.Social.Type.Tiktok
                    )
                )
            ),

            StreamerEntity(
                userName = "Lutti",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_lutti_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/lutti",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/luttitv",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            ),

            StreamerEntity(
                userName = "Ponce",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_ponce_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/ponce",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/poncetwitch",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            )
        )
    }

    override suspend fun getInLiveStreamers(): List<StreamerEntity> {
        return listOf(
            StreamerEntity(
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

            StreamerEntity(
                userName = "Kameto",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_kameto_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/kamet0",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/kametol0l",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            ),

            StreamerEntity(
                userName = "AmineMatue",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_amine_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/aminematue",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.tiktok.com/@aminematue",
                        StreamerEntity.Social.Type.Tiktok
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/aminematue",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            ),

            StreamerEntity(
                userName = "MisterV",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_misterv_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.youtube.com/@misterv",
                        StreamerEntity.Social.Type.Youtube
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/yvick",
                        StreamerEntity.Social.Type.Instagram
                    ),
                    StreamerEntity.Social(
                        "https://www.tiktok.com/@misterv",
                        StreamerEntity.Social.Type.Tiktok
                    )
                )
            ),

            StreamerEntity(
                userName = "Maghla",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_maghla_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/maghla",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/maghlarnaud",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            ),

            StreamerEntity(
                userName = "Inoxtag",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_inoxtag_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.youtube.com/@Inoxtag",
                        StreamerEntity.Social.Type.Youtube
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/inoxtag",
                        StreamerEntity.Social.Type.Instagram
                    ),
                    StreamerEntity.Social(
                        "https://www.tiktok.com/@inoxtag",
                        StreamerEntity.Social.Type.Tiktok
                    )
                )
            ),

            StreamerEntity(
                userName = "Michou",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_michou_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/michou",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.youtube.com/@Michou",
                        StreamerEntity.Social.Type.Youtube
                    ),
                    StreamerEntity.Social(
                        "https://www.tiktok.com/@michou_yt",
                        StreamerEntity.Social.Type.Tiktok
                    )
                )
            ),

            StreamerEntity(
                userName = "BagheraJones",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_baghera_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/bagherajones",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/bagherajones",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            ),

            StreamerEntity(
                userName = "Etoiles",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_etoiles_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/etoiles",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.youtube.com/@EtoilesTwitch", StreamerEntity.Social.Type.Youtube
                    )
                )
            ),

            StreamerEntity(
                userName = "Domingo",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_domingo_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/domingo",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.youtube.com/@DomingoReplay", StreamerEntity.Social.Type.Youtube
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/paolodomingo",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            ),

            StreamerEntity(
                userName = "Jury (J大きい)",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_tiktok_creator_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.tiktok.com/@khaby.lame",
                        StreamerEntity.Social.Type.Tiktok
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/khaby00",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            ),

            StreamerEntity(
                userName = "Grimkujow",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_grim_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/grimkujow",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.tiktok.com/@grimkujow_", StreamerEntity.Social.Type.Tiktok
                    )
                )
            ),

            StreamerEntity(
                userName = "Lutti",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_lutti_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/lutti",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/luttitv",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            ),

            StreamerEntity(
                userName = "Ponce",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_ponce_placeholder_url",
                socials = listOf(
                    StreamerEntity.Social(
                        "https://www.twitch.tv/ponce",
                        StreamerEntity.Social.Type.Twitch
                    ),
                    StreamerEntity.Social(
                        "https://www.instagram.com/poncetwitch",
                        StreamerEntity.Social.Type.Instagram
                    )
                )
            )
        )
    }
}