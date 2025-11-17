package com.example.financeiro;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Carrega o layout principal (que tem o contêiner e a barra de navegação)
        setContentView(R.layout.activity_main);

        // 2. Encontra a barra de navegação
        BottomNavigationView navView = findViewById(R.id.bottom_nav_view);

        // 3. Encontra o "host" de navegação (o espaço onde os fragmentos são carregados)
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        // 4. Pega o "controlador" que gerencia a troca de fragmentos
        NavController navController = navHostFragment.getNavController();

        // 5. Conecta a barra de navegação ao controlador
        // Isso faz com que clicar em um ícone na barra (ex: "Dívidas")
        // automaticamente carregue o fragmento correto no contêiner.
        NavigationUI.setupWithNavController(navView, navController);

        // Toda a lógica de UI (botões, listas) e banco de dados
        // foi movida para os seus respectivos Fragmentos.

    }
}