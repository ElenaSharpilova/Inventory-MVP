package android.example.myinventory_mvp_neobis.view.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.example.myinventory_mvp_neobis.R
import android.example.myinventory_mvp_neobis.contract.ContractInterface
import android.example.myinventory_mvp_neobis.databinding.FragmentAddItemBinding
import android.example.myinventory_mvp_neobis.model.Item
import android.example.myinventory_mvp_neobis.presenter.Presenter
import android.example.myinventory_mvp_neobis.repository.Repository
import android.example.myinventory_mvp_neobis.view.InventoryApplication
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddItemFragment : Fragment(), ContractInterface.View {

    lateinit var item: Item
    lateinit var imageUrl: Uri
    lateinit var image: Bitmap
    private val presenter by lazy { Presenter(Repository((activity?.application as InventoryApplication).database.itemDao())) }

    private lateinit var binding: FragmentAddItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_item, container, false)
        return binding.root
    }

    // Returns true if the EditTexts are not empty
    override fun isEntryValid(): Boolean {
        return presenter.isEntryValid(
            binding.itemName.text.toString(),
            binding.itemPrice.text.toString(),
            binding.itemCount.text.toString(),
            binding.itemSupplier.text.toString()
        )
    }

    //Inserts the new Item into database and navigates up to list fragment.
    override fun addNewItem() {
        if (isEntryValid()) {
            val itemToInsert = Item(
                0,
                binding.itemName.text.toString(),
                binding.itemCount.text.toString().toInt(),
                binding.itemSupplier.text.toString(),
                binding.itemPrice.text.toString().toInt(),
                image
            )
            presenter.insert(itemToInsert)
            val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
            findNavController().navigate(action)
        } else Toast.makeText(
            requireContext(),
            "Entry can not be empty", Toast.LENGTH_SHORT
        ).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(view)
        binding.saveAction.setOnClickListener {
            addNewItem()
        }
        binding.uploadButton.setOnClickListener {
            openGalleryForImage()
        }

    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imageUrl = data?.data!!
                binding.image.setImageURI(imageUrl)
                CoroutineScope(Dispatchers.IO).launch {
                    image = getBitmap(imageUrl)
                }
            }
        }

    override fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private suspend fun getBitmap(data: Uri?): Bitmap {
        val loading = ImageLoader(requireContext())
        val request = ImageRequest.Builder(requireContext())
            .data(data)
            .build()
        val result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        presenter.detachView()
    }
}