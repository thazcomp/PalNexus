# PalHelper

Calculadora de breeding do Palworld para Android, em **Kotlin + Jetpack Compose**, seguindo
**TDD**, com **Flow/StateFlow** e **Coroutines** para o gerenciamento de estado assíncrono.

## O que o app faz

Visual em estilo "janela de HUD" azul, inspirado nas interfaces do próprio jogo: painéis com
gradiente azul-marinho e cantos decorados tipo scanner sci-fi (veja `GameWindowPanel`), com
destaques em dourado/ciano. Cada Pal exibe seu ícone real, carregado ao vivo da wiki da
comunidade pela internet (veja a seção "Sobre as imagens" abaixo) — sem imagem disponível, um
selo colorido com a inicial do nome aparece no lugar.

O app abre num **menu inicial** (`HomeMenuScreen`) onde o usuário escolhe entre duas
ferramentas:

### 1. Calculadora de Breeding

Duas telas (abas), cobrindo os dois sentidos pedidos:

1. **2 Pals → Filho**: escolha dois Pals e veja qual Pal nasce do cruzamento.
2. **Filho → Pais**: escolha um Pal desejado e veja todas as combinações de pais (combos
   especiais, o par da mesma espécie, e todo par cuja fórmula padrão resolve para ele).

### 2. Calculadora de Recursos

Uma grade (`ResourceCalculatorScreen`) com as construções da base, cada uma com seu ícone. Ao
tocar numa construção, um diálogo mostra o custo de materiais dela e permite adicionar N
unidades à lista de construção. Um painel no rodapé soma **todos os materiais necessários**
para construir tudo que foi selecionado, combinando materiais repetidos entre construções
diferentes (ex.: madeira do baú + madeira do Palbox viram uma linha só). A lógica de soma está
em `ResourceCalculator`, testada por TDD em `ResourceCalculatorTest` (e o estado reativo em
`ResourceCalculatorViewModelTest`, via Turbine).

Os custos e ícones das construções vêm de `palworld.gg/structures` (dados do 1.0). É um
conjunto representativo das construções essenciais de cada categoria (fundação, produção,
armazenamento, Pals, infraestrutura, comida, defesa, iluminação), não a lista completa de 490+
estruturas do jogo — dá para estender adicionando entradas em `StructureDataSource.kt`.

## Como o breeding é calculado

Em vez de tentar cravar manualmente centenas de combinações, o app implementa o **mecanismo
real do jogo** (fonte: [Palworld Wiki — Breeding](https://palworld.wiki.gg/wiki/Breeding),
dados atualizados para o patch 1.0):

- Cada Pal tem um número oculto de **poder de cruzamento** ("Combi Rank").
- `childRank = floor((rankPaiA + rankPaiB + 1) / 2)`
- O filho é o Pal *elegível* cujo rank está mais perto desse valor; em caso de empate, vence
  o Pal com o menor índice interno do jogo (Paldeck index).
- Duas exceções à fórmula:
  - **Mesma espécie**: sempre gera a mesma espécie.
  - **Combos especiais fixos**: alguns pares sempre resultam em um Pal específico
    (ex.: Relaxaurus + Sparkit → Relaxaurus Lux), independente da fórmula.
  - Alguns Pals só podem nascer cruzando dois da própria espécie (ex.: Jetragon, Paladius).

Essa lógica está isolada em [`BreedingCalculator`](app/src/main/java/com/palhelper/app/domain/BreedingCalculator.kt),
testada por TDD em [`BreedingCalculatorTest`](app/src/test/java/com/palhelper/app/domain/BreedingCalculatorTest.kt).

### Sobre as imagens

Os ícones dos Pals **não estão embutidos no app** — a arte pertence à Pocketpair. Em vez disso,
`Pal.iconUrl` guarda um link para o ícone hospedado pela [Palworld Wiki](https://palworld.wiki.gg),
e o app carrega essa imagem pela internet em tempo real (usando [Coil](https://coilkt.github.io/coil/)),
da mesma forma que um navegador exibiria a imagem de uma página. É por isso que o app pede a
permissão `INTERNET` no `AndroidManifest.xml`. Pals sem URL capturada (alguns lançados no patch
1.0 mais recente) mostram um selo colorido com a inicial no lugar do ícone.

Para evitar que os ícones "apareçam aos poucos" conforme a lista rola, o app pré-carrega todos
os ícones no `PalHelperRoot`/`preloadPalIcons` antes de mostrar a tela principal — uma tela de
carregamento (`PalIconLoadingScreen`) exibe o progresso (`X / Y imagens carregadas`) enquanto
isso. Se a rede estiver lenta ou indisponível, um timeout de 20s garante que o app abre mesmo
assim (ícones que não carregaram a tempo caem no selo de fallback).

### Sobre o dataset (e por que ele pode errar)

Os valores de rank em [`PalDataSource`](app/src/main/java/com/palhelper/app/data/PalDataSource.kt)
foram extraídos da wiki comunitária, cobrindo boa parte do elenco de Pals do jogo 1.0 (140+
espécies + variantes), além de ~40 combos especiais e a lista de Pals "mesma espécie apenas".

**Importante:** a Pocketpair (desenvolvedora) nunca publicou oficialmente os valores de
"breeding power" de cada Pal. Todo esse dado vem de datamining/engenharia reversa feita pela
comunidade — e o patch 1.0 (10/jul/2026) reembaralhou esses valores para acomodar ~72 Pals
novos. Isso significa que **fontes diferentes divergem entre si**, e pelo menos um caso já foi
confirmado errado no dataset original (Nitewing + Helzephyr deveria dar Azurobe, não o que a
fórmula calculava com os ranks da wiki — corrigido como override verificado, veja o comentário
em `specialCombinations`).

Se você testar uma combinação no jogo ou em outro site (como o palpedia.net) e o resultado
bater diferente do app, me avise com o par exato e o resultado esperado — eu registro como uma
correção verificada em vez de tentar adivinhar novos valores de rank (o que arriscaria quebrar
silenciosamente outros pares que já estão certos).

**Atualização (checagem cruzada pós-1.0):** fui atrás de uma fonte melhor e achei o
[PalCalc](https://github.com/tylercamp/palcalc), um solver open-source que extrai os dados
direto dos arquivos do jogo (`DT_PalMonsterParameter`) — é o mais próximo de uma fonte
"oficial" que existe. Usei a tabela de combos especiais documentada por ele (via drawpie.com)
para achar e corrigir **3 Pals que estavam completamente inalcançáveis no app** (Surfent Terra,
Reptyro Cryst e Vanwyrm Cryst não tinham nenhum combo especial cadastrado). Isso já está
corrigido em `specialCombinations`, com teste de regressão.

Porém, isso também confirmou que **nem as fontes "verificadas para 1.0" concordam entre si**:
testando Penking + Bushi, a fórmula com os ranks que tenho dá Anubis, mas o drawpie.com (que
usa dados do PalCalc) diz que o resultado correto agora é Sibelyx. Até o próprio PalCalc tem um
[bug aberto de resultado errado](https://github.com/tylercamp/palcalc/issues/196) reportado
há poucos dias. Ou seja: mesmo semanas após o patch 1.0, ninguém tem uma tabela 100% validada
— o que faz do fluxo de correção verificada (você testa, me avisa, eu registro) o caminho mais
confiável disponível agora, em vez de eu trocar a tabela inteira por outra fonte igualmente
não-oficial e arriscar piorar em vez de melhorar.

Também vale registrar uma limitação conhecida: Katress + Wixen tem um resultado que depende do
**gênero** dos pais (fêmea Wixen + Katress = Wixen Noct; fêmea Katress + Wixen = Katress Ignis).
O app ainda não modela gênero de Pal, então só o resultado Katress Ignis é alcançável por
enquanto (documentado no comentário ao lado dessa entrada em `specialCombinations`).

**Atualização do elenco 1.0 (Valentail etc.):** após a confirmação em jogo de que
Gobfin + Suzaku = Valentail — um Pal que nem existia na lista — ficou claro que o dataset
estava sem os ~44 Pals novos do 1.0. Eles foram adicionados à lista (Valentail, Snock, Mycora,
Dynamoff, Neptilius, e todos os demais), **porém com rank nulo por enquanto**: os ranks
datamineados desses Pals ainda não têm publicação confiável (as fontes conflitam entre si),
então eles aparecem no seletor e funcionam via combos verificados, mas ainda não participam da
fórmula de média. Consequência importante e honesta: **resultados da fórmula envolvendo faixas
de rank onde esses Pals novos se encaixariam continuarão divergindo do jogo** até os ranks
reais serem preenchidos — é exatamente por isso que Gobfin + Suzaku dava outro Pal antes.
Além disso, a tabela de combos especiais foi substituída pela versão **testada pós-1.0** da
wiki Fandom, que restaurou caminhos para Pals que estavam inalcançáveis no app (Anubis via
Penking+Bushi, Lyleen, Grizzbolt, Faleris, Orserk, Shadowbeak, Kitsun, Maraith, Astegon,
Cryolinx, Helzephyr e Suzaku Aqua), e Selyne/Warsect Terra/Neptilius entraram na lista de
"mesma espécie apenas". Tudo coberto por testes de regressão.

**Não é 100% exaustivo** — o Paldeck completo tem mais de 280 Pals. Para adicionar um Pal
que falta, basta:

```kotlin
Pal("novo_pal_id", "Nome Exibido", breedingPower = 123, paldeckIndex = 456)
```

E, se ele tiver um combo fixo, adicionar em `specialCombinations`. A fórmula em si não muda.

## Arquitetura

```
app/src/main/java/com/palhelper/app/
├── data/
│   ├── model/Pal.kt              # modelo de domínio
│   ├── PalDataSource.kt          # dataset estático (rank, índice, combos especiais)
│   └── PalRepository.kt          # expõe os Pals como StateFlow
├── domain/
│   └── BreedingCalculator.kt     # fórmula de breeding (puro, sem Android)
└── ui/
    ├── BreedingViewModel.kt      # estado da UI via StateFlow, cálculo em Coroutines
    ├── BreedingApp.kt            # abas raiz
    ├── screens/                  # as duas telas (Compose)
    ├── components/PalPickerField.kt  # seletor de Pal com busca
    └── theme/                    # Material 3
```

## Stack técnica

- **Kotlin** 2.1 com `kotlin.plugin.compose` (novo plugin de compilador do Compose)
- **Jetpack Compose** + Material 3, `compose-bom` 2024.12
- **Coroutines** 1.9 + `StateFlow` para estado reativo no ViewModel
- **Navegação** por abas simples (não há telas suficientes para justificar Navigation Compose,
  mas a dependência já está no `libs.versions.toml` caso o app cresça)
- **TDD**: JUnit4 + [MockK](https://mockk.io/) + [Turbine](https://github.com/cashapp/turbine)
  (para testar `Flow`/`StateFlow`) + `kotlinx-coroutines-test`

## Como rodar

1. Abra a pasta `PalHelper` no Android Studio (versão atual, com suporte a AGP 8.7+ e Kotlin 2.1).
2. Deixe o Gradle sincronizar (vai baixar o wrapper 8.10.2 automaticamente).
3. Rode a configuração `app` num emulador ou dispositivo (minSdk 26).

## Como rodar os testes

```
./gradlew testDebugUnitTest
```

Os testes cobrem:
- `BreedingCalculatorTest`: a fórmula, o desempate por índice, os combos especiais, e a busca
  reversa de pares de pais.
- `BreedingViewModelTest`: que o `StateFlow` da UI reage corretamente à seleção de Pals nos
  dois modos (via Turbine).

## Próximos passos sugeridos

- Completar o dataset com os ~140 Pals restantes do Paldeck.
- Adicionar filtro por elemento/tipo na busca de Pals.
- Persistir os Pals "favoritos" do jogador (ex.: com o `window.storage` se virar um artifact,
  ou com DataStore se continuar como app nativo).
