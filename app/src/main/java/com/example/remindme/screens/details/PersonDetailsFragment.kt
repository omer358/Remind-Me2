package com.example.remindme.screens.details

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.remindme.R
import com.example.remindme.database.PeopleDatabase
import com.example.remindme.databinding.PersonDetailsFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.people_list_item.*

class PersonDetailsFragment : Fragment() {

    private val args: PersonDetailsFragmentArgs by navArgs()
    private lateinit var dataBinding: PersonDetailsFragmentBinding
    private lateinit var personDetailsViewModel:PersonDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.person_details_fragment,parent,false)
        val application = requireNotNull(this.activity).application
        val dataSource = PeopleDatabase.getInstance(application).peopleDao
        val personId: Long = args.personId

        val viewModelFactory = PersonDetailsViewModelFactory(application,dataSource,personId)
        val viewModel : PersonDetailsViewModel by viewModels{viewModelFactory}
        personDetailsViewModel = viewModel

        dataBinding.viewModel = viewModel
        dataBinding.lifecycleOwner = viewLifecycleOwner

        viewModel.navigateBack.observe(viewLifecycleOwner, Observer {
            if(it) {
                Log.i(TAG,"the observer is active and its value is $it")
                this.findNavController().navigateUp()
                viewModel.doneNavigateBack()
            }
        })

        setHasOptionsMenu(true)
        createNotificationChannel()

        return dataBinding.root
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "REMIND-ME"
            val descriptionText = "to remind you of people "
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("id", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = requireActivity()
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.person_details_menu,menu)
        Log.i(TAG,"the optionsMenu created!")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var itemId = item.itemId
        if (itemId == R.id.delete_person_item){
            Log.i(TAG,"the delete Item has been selected!")
            showDeleteDialog()
        }
        if(itemId == R.id.noitfication_item){
            personDetailsViewModel.startNotification()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteDialog() {
        MaterialAlertDialogBuilder(context)
            .setTitle(resources.getString(R.string.title_delete_person))
            .setMessage(resources.getString(R.string.supporting_text))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.delete)) { dialog, _ ->
                personDetailsViewModel.deletePerson(args.personId)
                dialog.cancel()
            }
            .show()
    }

    companion object{
        private const val TAG = "PersonDetailsFragment"
    }
}