package com.example.config

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.cio.*

object SupabaseConfig {
    // TODO: Replace with your Supabase credentials
    // Get them from https://supabase.com/dashboard/project/_/settings/api
    private const val SUPABASE_URL = "YOUR_SUPABASE_URL_HERE"
    private const val SUPABASE_KEY = "YOUR_SUPABASE_ANON_KEY_HERE"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest)
        
        httpEngine = CIO.create()
    }
}
