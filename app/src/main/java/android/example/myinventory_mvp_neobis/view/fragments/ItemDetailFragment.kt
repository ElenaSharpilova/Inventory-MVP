package android.example.myinventory_mvp_neobis.view.fragments

import android.example.myinventory_mvp_neobis.R
import android.example.myinventory_mvp_neobis.databinding.FragmentItemDetailBinding
import android.example.myinventory_mvp_neobis.presenter.Presenter
import android.example.myinventory_mvp_neobis.repository.Repository
import android.example.myinventory_mvp_neobis.view.InventoryApplication
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

class ItemDetailFragment : Fragment() {

    private val args by navArgs<ItemDetailFragmentArgs>()

    private val presenter by lazy { Presenter(Repository((activity?.application as InventoryApplication).database.itemDao())) }


    lateinit var binding: FragmentItemDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_detail, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(view)
        with(binding) {
            //set all text fields to current item's values
            itemName.text = args.currentItem.itemName
            itemPrice.text = args.currentItem.itemPrice.toString()
            itemCount.text = args.currentItem.itemQuantity.toString()
            itemSupplier.text = args.currentItem.itemSupplier
            itemImg.setImageBitmap(args.currentItem.itemImage)
            //logic for add item button
            addItem.setOnClickListener {
                val action = ItemDetailFragmentDirections.actionItemDetailFragmentToEditItemFragment(args.currentItem)
                findNavController().navigate(action)
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteALl -> alertDialog()
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }

    private fun alertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Are you sure you want to delete this item?")
        builder.setMessage("You will not be able to recover your data after deleting it")
        builder.setPositiveButton("Yes") { _, _ ->
            presenter.delete(args.currentItem)
            val action = ItemDetailFragmentDirections.actionItemDetailFragmentToItemListFragment()
            findNavController().navigate(action)
        }
        builder.setNegativeButton("No") { _, _ ->
            Toast.makeText(requireContext(),
                "Canceled", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }
}