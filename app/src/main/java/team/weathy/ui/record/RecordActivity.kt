package team.weathy.ui.record

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import team.weathy.databinding.ActivityRecordBinding
import team.weathy.ui.record.start.RecordStartFragment

class RecordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.root.id, RecordStartFragment(), "record").commit()
    }

    fun replaceFragment(fragment : Fragment){
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.replace(binding.root.id, fragment, "record").commit()
    }

    fun finishFragment(fragment : Fragment){
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.remove(fragment).commit()
        supportFragmentManager.popBackStack()
    }
}