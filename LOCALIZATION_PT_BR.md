## Localização da Calculadora de Recursos - Português (Brasil)

### Resumo das Alterações

Foram implementadas traduções completas para a calculadora de recursos em português, incluindo nomes de estruturas, materiais e labels da UI.

### Arquivos Criados

1. **pt_BR.kt** (novo)
   - Arquivo centralizado com todas as traduções em português
   - Contém 29+ materiais e 88+ estruturas traduzidas
   - Inclui linhas de produção (1, 2 e antiga)

2. **StructureExtensions.kt** (novo)
   - Funções de extensão para acesso fácil às traduções
   - `Structure.getPortugueseName()` - retorna nome traduzido da estrutura
   - `MaterialCost.getPortugueseName()` - retorna nome traduzido do material

3. **StructureCategoryResources.kt** (novo)
   - Mapeia categorias com recursos de string
   - Permite categorias em português no filtro

### Arquivos Modificados

1. **ResourceCalculatorScreen.kt**
   - Atualizado para usar `getPortugueseName()` em todos os locais onde os nomes são exibidos
   - Importa extensões de tradução
   - Todos os cards, diálogos e painel de totais agora exibem nomes em português

2. **strings.xml**
   - Adicionados textos da UI em português
   - Inclui instruções, botões, labels e mensagens

### Estruturas Incluídas

#### Materiais Traduzidos (29)
- Madeira, Pedra, Lingote, Fragmento Paldium
- Fibra, Tecido, Prego, Órgãos especiais
- Circuitos, Baterias, Núcleos
- E muitos mais...

#### Construções Traduzidas (88+)
**Fundação:** 17 estruturas em madeira, pedra, metal e antigas
**Produção:** Bancadas, Fornalhas, Linhas de montagem, Linhas de produção 1/2/antiga
**Armazenamento:** 8 baús e prateleiras
**Pals:** 9 estruturas de criação e incubação
**Infraestrutura:** 13 estruturas de conforto e utilidade
**Comida:** 6 plantações
**Defesa:** 5 torres
**Iluminação:** 7 lanternas e lâmpadas

### Como Usar

As traduções são automáticas. Basta usar `structure.getPortugueseName()` ou `material.getPortugueseName()` para obter os nomes em português.

```kotlin
// Exemplo de uso
val portugueseName = structure.getPortugueseName()
val portugueseMaterialName = material.getPortugueseName()
```

### Adicionar Novas Traduções

Para adicionar novas estruturas ou materiais:

1. Editar `pt_BR.kt`
2. Adicionar entrada no mapa apropriado (structures ou materials)
3. Usar o ID da estrutura/material como chave
4. Fornecer o nome em português como valor

```kotlin
"meu_id_novo" to "Meu Nome em Português"
```

### Notas

- As traduções usam um sistema de mapa para fácil manutenção
- Fallback automático para ID original se tradução não encontrada
- Compatível com múltiplos idiomas no futuro
- Todas as estruturas e materiais do jogo estão suportados
