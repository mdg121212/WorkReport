package com.mattg.aztecworkreport.helpers


import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

open class BaseFragment: Fragment() {
    /**
     * variables to reference a main and background coroutine scope for database and
     * repository operations.
     */
     val coroutineScope = CoroutineScope(Dispatchers.Default)
     val mainScope = CoroutineScope(Dispatchers.Main)


}