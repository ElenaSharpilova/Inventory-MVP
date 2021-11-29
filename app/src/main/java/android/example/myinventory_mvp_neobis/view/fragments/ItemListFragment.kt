package android.example.myinventory_mvp_neobis.view.fragments

import android.example.myinventory_mvp_neobis.R
import android.example.myinventory_mvp_neobis.contract.Delegates
import android.example.myinventory_mvp_neobis.databinding.FragmentItemListBinding
import android.example.myinventory_mvp_neobis.model.Item
import android.example.myinventory_mvp_neobis.presenter.Presenter
import android.example.myinventory_mvp_neobis.repository.Repository
import android.example.myinventory_mvp_neobis.view.InventoryApplication
import android.example.myinventory_mvp_neobis.view.ItemAdapter
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

class ItemListFragment : Fragment(), Delegates.RecyclerItemClicked {

    private val presenter by lazy { Presenter(Repository((activity?.application as InventoryApplication).database.itemDao())) }
    private var _binding: FragmentItemListBinding? = null
    private val binding: FragmentItemListBinding
        get() {
            return _binding!!
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(view)
        binding.apply {
            rcView.layoutManager = LinearLayoutManager(requireContext())
            addItem.setOnClickListener {
                val action = ItemListFragmentDirections.actionItemListFragmentToAddItemFragment()
                findNavController().navigate(action)
            }
        }
        presenter.getALl().observe(viewLifecycleOwner) {
            binding.rcView.adapter = ItemAdapter(it, this)
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

    private fun alertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Are you sure you want to delete all items?")
        builder.setMessage("You will not be able to recover your data after deleting it")
        builder.setPositiveButton("Yes") { _, _ ->
            presenter.clear()
        }

        builder.setNegativeButton("No") { _, _ ->
            Toast.makeText(
                requireContext(),
                "Canceled", Toast.LENGTH_SHORT
            ).show()
        }
        builder.show()
    }

    override fun onItemClick(item: Item) {
        val action = ItemListFragmentDirections.actionItemListFragmentToItemDetailFragment(item)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }
}
