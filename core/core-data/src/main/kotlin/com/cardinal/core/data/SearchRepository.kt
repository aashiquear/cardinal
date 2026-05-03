package com.cardinal.core.data

import com.cardinal.core.domain.Place
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun search(query: String): Flow<List<Place>>
}
