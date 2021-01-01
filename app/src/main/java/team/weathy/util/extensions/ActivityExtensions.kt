package team.weathy.util.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit

fun AppCompatActivity.replaceFragment(containerView: FragmentContainerView, clazz: Class<out Fragment>) {
    val tagName = clazz.simpleName
    val exists = supportFragmentManager.findFragmentByTag(tagName)

    supportFragmentManager.commit {
        exists?.run {
            replace(containerView.id, exists)
        } ?: replace(
            containerView.id, clazz, null, tagName
        )
    }
}

fun AppCompatActivity.addFragment(containerView: FragmentContainerView, clazz: Class<out Fragment>) {
    val tagName = clazz.simpleName

    supportFragmentManager.commit {
        add(containerView.id, clazz, null, tagName)
        addToBackStack(tagName)
    }
}