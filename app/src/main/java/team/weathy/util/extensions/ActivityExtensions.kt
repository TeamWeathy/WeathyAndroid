package team.weathy.util.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import team.weathy.R

fun AppCompatActivity.replaceFragment(
    containerView: FragmentContainerView, clazz: Class<out Fragment>, addToBackStack: Boolean = false
) {
    val tagName = clazz.simpleName
    val exists = supportFragmentManager.findFragmentByTag(tagName)

    supportFragmentManager.commit {
        setCustomAnimations(
            R.anim.enter_from_right, R.anim.exit_to_left, R.anim.pop_enter_from_left, R.anim.pop_exit_to_right
        )
        exists?.run {
            replace(containerView.id, exists)
        } ?: replace(
            containerView.id, clazz, null, tagName
        )
        if (addToBackStack) addToBackStack(tagName)
    }
}

fun AppCompatActivity.addFragment(containerView: FragmentContainerView, clazz: Class<out Fragment>) {
    val tagName = clazz.simpleName

    supportFragmentManager.commit {
        add(containerView.id, clazz, null, tagName)
        addToBackStack(tagName)
    }
}

fun AppCompatActivity.popFragmentIfExist(clazz: Class<out Fragment>) {
    val tagName = clazz.simpleName
    val exists = supportFragmentManager.findFragmentByTag(tagName)

    if (exists != null) {
        supportFragmentManager.popBackStack(tagName, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}