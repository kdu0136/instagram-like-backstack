package com.smihajlovski.instabackstack.ui.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.smihajlovski.instabackstack.tmp.NavigatorDestination
import com.smihajlovski.instabackstack.tmp.createDataBinding

abstract class BaseFragment<VB : ViewDataBinding, NT : NavigatorDestination>(@LayoutRes private val resId: Int) :
        Fragment(), IBaseFragmentAct {

    companion object {
        protected var currentTab: String = ""
    }

    protected val fragmentTag = javaClass.simpleName

    protected var fragmentInteractionCallback: IFragmentInteraction? = null

    private lateinit var mBinding: VB
    protected val binding: VB
        get() {
            return if (::mBinding.isInitialized) mBinding
            else throw NullPointerException("ViewDataBinding is Null or Not Initialized.")
        }

    /**
     * page UI setup
     */
    abstract fun onSetupUI()

    /**
     * fragment back press function
     *
     * if override this function, return value should be true.
     * return value means create function overriding or not
     */
    override fun onBackPressed(): Boolean {
        printFragmentLifecycle(name = object {}.javaClass.enclosingMethod?.name.toString())
        return false
    }

    /**
     * page view model observe setting
     */
    abstract fun observeViewModel()

    abstract fun runNavigate(navigate: NT)

    override fun onAttach(context: Context) {
        printFragmentLifecycle(name = object {}.javaClass.enclosingMethod?.name.toString())
        super.onAttach(context)
        try {
            fragmentInteractionCallback = context as IFragmentInteraction
        } catch (e: ClassCastException) {
            throw RuntimeException("$context must implement ${IFragmentInteraction::class.java.simpleName}")
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        printFragmentLifecycle(name = object {}.javaClass.enclosingMethod?.name.toString())
        super.onCreateView(inflater, container, savedInstanceState)

        mBinding = createDataBinding(resId = resId, context = requireContext())
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        printFragmentLifecycle(name = object {}.javaClass.enclosingMethod?.name.toString())
        super.onViewCreated(view, savedInstanceState)

        onSetupUI()

        observeViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        printFragmentLifecycle(name = object {}.javaClass.enclosingMethod?.name.toString())
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        printFragmentLifecycle(name = object {}.javaClass.enclosingMethod?.name.toString())
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        printFragmentLifecycle(name = object {}.javaClass.enclosingMethod?.name.toString())
        super.onStart()
    }

    override fun onResume() {
        printFragmentLifecycle(name = object {}.javaClass.enclosingMethod?.name.toString())
        super.onResume()
    }

    override fun onPause() {
        printFragmentLifecycle(name = object {}.javaClass.enclosingMethod?.name.toString())

        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        printFragmentLifecycle(name = object {}.javaClass.enclosingMethod?.name.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        printFragmentLifecycle(name = object {}.javaClass.enclosingMethod?.name.toString())
        super.onStop()
    }


    override fun onDestroyView() {
        printFragmentLifecycle(name = object {}.javaClass.enclosingMethod?.name.toString())

        super.onDestroyView()
    }

    override fun onDestroy() {
        printFragmentLifecycle(name = object {}.javaClass.enclosingMethod?.name.toString())
//        viewModel.endFragment(fragName = javaClass.simpleName)
        super.onDestroy()
    }

    override fun onDetach() {
        printFragmentLifecycle(name = object {}.javaClass.enclosingMethod?.name.toString())
        fragmentInteractionCallback = null
        super.onDetach()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        PrintLog.d("ActivityResult", "resultCode: $resultCode", fragmentTag)
//        PrintLog.d("ActivityResult", "requestCode: $requestCode", fragmentTag)
    }

    private fun printFragmentLifecycle(name: String) {
//        PrintLog.d(javaClass.simpleName, name, fragmentTag)
    }
}
