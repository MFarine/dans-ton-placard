package com.sem.lamoot.elati.danstonplacard.danstonplacard.view.fragment.ldc;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sem.lamoot.elati.danstonplacard.danstonplacard.R;
import com.sem.lamoot.elati.danstonplacard.danstonplacard.database.RoomDB;
import com.sem.lamoot.elati.danstonplacard.danstonplacard.database.converter.DateTypeConverter;
import com.sem.lamoot.elati.danstonplacard.danstonplacard.database.dao.ListeCoursesDao;
import com.sem.lamoot.elati.danstonplacard.danstonplacard.database.model.ListeCourses;
import com.sem.lamoot.elati.danstonplacard.danstonplacard.database.model.Produit;
import com.sem.lamoot.elati.danstonplacard.danstonplacard.view.fragment.AjouterProduitFragment;
import com.sem.lamoot.elati.danstonplacard.danstonplacard.view.fragment.DetailProduitFragment;
import com.sem.lamoot.elati.danstonplacard.danstonplacard.view.fragment.inventaire.ProduitAdapter;
import com.sem.lamoot.elati.danstonplacard.danstonplacard.viewmodel.ListeCoursesViewModel;

import java.util.List;

public class LDCEditFragment extends Fragment
        implements ProduitAdapter.OnMinusImageViewClickListener,
        ProduitAdapter.OnAddImageViewClickListener,
        ProduitAdapter.OnProductItemClickListener {

    public static String ARG_LDC = "";
    private Context mContext;
    private int idLdc;

    private ImageButton ldcSaveEdit;
    private RecyclerView ldcEditProductRecyclerView;
    private FloatingActionButton ldcEditAddProduct;
    private TextInputLayout ldcInputLayout;
    private EditText ldcNameEdit;

    private ListeCoursesDao listeCoursesDao;
    private ListeCourses listeCourse;

    public static Fragment newInstance(Integer param){
        Bundle args = new Bundle();
        args.putInt(ARG_LDC, param);
        LDCEditFragment ldcEditFragment = new LDCEditFragment();
        ldcEditFragment.setArguments(args);
        return ldcEditFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getContext();
        if (getArguments() != null) {
            idLdc = getArguments().getInt(ARG_LDC);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        firebaseAnalytics.setCurrentScreen(this.getActivity(), this.getClass().getSimpleName(), this.getClass().getSimpleName());

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ldc_edit_fragment, container, false);

        listeCoursesDao = RoomDB.getDatabase(mContext).listeCoursesDao();

        ListeCoursesViewModel listeCoursesViewModel = ViewModelProviders.of(this).get(ListeCoursesViewModel.class);

        ldcSaveEdit = view.findViewById(R.id.btn_save_ldc_edit);
        ldcInputLayout = view.findViewById(R.id.ldc_edit_name_layout);
        ldcNameEdit = view.findViewById(R.id.ldc_edit_name);
        ldcEditAddProduct = view.findViewById(R.id.ldc_edit_ajout_produit_fab);

        TextView ldcEditDefaultContent = view.findViewById(R.id.ldc_edit_default_content);

            if(idLdc == LDCFragment.NEW_LDC)
            {
                listeCourse = new ListeCourses("");
                String defaultName = "Liste du " + DateTypeConverter.DATE_FORMATTER.format(listeCourse.getDateCreation());
                listeCourse.setNom(defaultName);
                idLdc = (int) listeCoursesDao.insert(listeCourse);

            }
            else{
                listeCourse = listeCoursesDao.getListeCoursesById(idLdc);
                if (!listeCourse.getProduitsAPrendre().isEmpty())
                    ldcEditDefaultContent.setVisibility(View.GONE);
            }

        ldcNameEdit.setText(listeCourse.getNom());

            ldcSaveEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!validateName()) {
                        return;
                    }

                listeCourse = listeCoursesDao.getListeCoursesById(idLdc);
                listeCourse.setNom(ldcNameEdit.getText().toString());
                listeCourse.setId(idLdc);
                listeCoursesDao.updateListe(listeCourse);

                getFragmentManager().popBackStack();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.root_ldc_frame, DetailLDCFragment.newInstance(idLdc));
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }



        });

        ldcEditAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the view for adding a product to the current piece
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.root_ldc_frame, AjouterProduitFragment.newInstance("DIVERS", idLdc));
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        ldcEditProductRecyclerView = view.findViewById(R.id.ldc_product_edit_recyclerview);
        ProduitAdapter produitAdapter = new ProduitAdapter(mContext, this, this, idLdc);
        produitAdapter.setOnProductItemClickListener(this::onProductItemClickListener);
        listeCoursesViewModel.getListeCoursesByIdLD(idLdc).observe(this, listeCourses1 -> produitAdapter.setData(listeCourses1.getProduitsAPrendre()));

        ldcEditProductRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ldcEditProductRecyclerView.setAdapter(produitAdapter);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(mContext);
        ldcEditProductRecyclerView.setLayoutManager(manager);
        ldcEditProductRecyclerView.setNestedScrollingEnabled(false);

        return view;
    }

    private long ajouterNouvelleListe(EditText ldcNameEdit) {
        String listeTitle = ldcNameEdit.getText().toString();
        ListeCourses listeCourses = new ListeCourses(listeTitle);

        return listeCoursesDao.insert(listeCourses);
    }

    private boolean validateName() {
        if (ldcNameEdit.getText().toString().trim().isEmpty()) {
            ldcInputLayout.setError(getString(R.string.err_msg_name));
            requestFocus(ldcNameEdit);
            return false;
        } else {
            ldcInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onMinusImageViewClickListener(Produit produit) {
        listeCourse = listeCoursesDao.getListeCoursesById(idLdc);
        List<Produit> aPrendre = listeCourse.getProduitsAPrendre();
        for(Produit p : aPrendre)
        {
            if(p.getId() == produit.getId() && p.getQuantite() > 0)
            {
                p.setQuantite(p.getQuantite() - 1);
            }
        }
        listeCourse.setProduitsAPrendre(aPrendre);
        listeCoursesDao.updateListe(listeCourse);
    }

    @Override
    public void onAddImageViewClickListener(Produit produit) {
        Log.d("dtp", "ADD PRODUCT");

        listeCourse = listeCoursesDao.getListeCoursesById(idLdc);
        List<Produit> aPrendre = listeCourse.getProduitsAPrendre();

        Log.d("dtp", "" + aPrendre.size());

        for(Produit p : aPrendre)
        {
            if(p.getId() == produit.getId()){
                p.setQuantite(p.getQuantite() + 1);
            }
        }
        listeCourse.setProduitsAPrendre(aPrendre);
        listeCoursesDao.updateListe(listeCourse);
    }

    @Override
    public void onProductItemClickListener(Produit produit) {
        // Launch the view for product's detail
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        String[] params = new String[]{produit.getId()+"", "" + idLdc};
        transaction.replace(R.id.root_ldc_frame, DetailProduitFragment.newInstance(params));
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}