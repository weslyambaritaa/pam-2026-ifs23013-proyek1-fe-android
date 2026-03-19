package org.delcom.pam_2026_ifs23013_proyek1_fe_android.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.prefs.AuthTokenPref

@Module
@InstallIn(SingletonComponent::class)
object PrefModule {
    @Provides
    fun provideAuthTokenPref(@ApplicationContext context: Context): AuthTokenPref {
        return AuthTokenPref(context)
    }
}