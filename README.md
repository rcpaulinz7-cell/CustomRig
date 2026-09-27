# CustomRig

Plugin Java (Paper/Purpur) que dá aos jogadores um "modelo customizado"
(rig) feito de Display Entities, com animações via comando — tudo
compatível com Java **e** Bedrock via Geyser, porque Display Entities são
uma feature vanilla do jogo (não exigem mod no cliente).

## O que este plugin NÃO é

- Não é o Figura mod nem o Emotecraft de verdade — não roda scripts Lua,
  não tem shaders, não anima partes finas de cabelo/roupa.
- É um sistema de rig fixo, com partes (cabeça, tronco, braços, pernas)
  definidas por você em JSON, e animações também definidas por você em JSON.
- Não inclui as texturas em si. Você precisa ter um **resource pack**
  separado, mapeando `material + CustomModelData` para o modelo/textura
  3D que você quer mostrar. Esse resource pack precisa ser hospedado e
  aplicado no servidor (`server.properties` -> `resource-pack` /
  `resource-pack-sha1`), e o Geyser converte automaticamente pro Bedrock
  (com algumas limitações de conversão, especialmente para geometria 3D
  complexa).

## Resource pack incluído (CustomRig-resourcepack.zip)

Diferente da entrega anterior, agora vem junto um **resource pack de verdade**,
já no formato correto para Minecraft 26.2 (pack_format 88, sistem novo de
`range_dispatch` para CustomModelData que passou a valer a partir da 1.21.4):

- `assets/minecraft/items/player_head.json` e `leather_horse_armor.json`:
  os "overrides" que fazem o CustomModelData 1001-1006 (os mesmos usados em
  `modelos/exemplo.json`) apontarem para os modelos customizados.
- `assets/customrig/models/item/*.json`: os modelos de cada parte do rig.
- `assets/customrig/textures/item/*.png`: **texturas placeholder** (retângulos
  de cor sólida gerados por script, não arte de verdade) — servem para você
  confirmar que o sistema funciona antes de pagar/fazer a arte final. Troque
  esses PNGs pelos designs reais depois, mantendo o mesmo nome de arquivo.

SHA1 do arquivo (para o `server.properties`, campo `resource-pack-sha1`):

```
0eb51347f2051971b540f5d3e1937b81439f4219
```

Esse hash é só deste arquivo exato — se você editar qualquer coisa dentro do
zip (inclusive trocar uma textura), o SHA1 muda e você precisa recalcular
(`sha1sum arquivo.zip` no Linux/Mac, ou `certutil -hashfile arquivo.zip SHA1`
no Windows).

### Como aplicar no servidor

1. Hospede o `.zip` em algum lugar com link direto de download (GitHub
   Releases, seu próprio site, etc). O Minecraft não aceita link do Google
   Drive/Dropbox direto — precisa ser um link que baixe o arquivo puro.
2. No `server.properties`:
   ```
   resource-pack=https://seusite.com/CustomRig-resourcepack.zip
   resource-pack-sha1=0eb51347f2051971b540f5d3e1937b81439f4219
   require-resource-pack=true
   ```
3. Reinicie o servidor. Jogadores Java vão receber o prompt de download.

### Bedrock / Geyser

O Geyser converte resource packs Java para o formato Bedrock automaticamente
quando você configura o mesmo pack no `config.yml` do Geyser (seção de
resource packs, apontando para a pasta `packs/` do Geyser — copie o mesmo
`.zip` para lá). Para modelos de item simples como estes (ícones 2D via
`item/generated`), a conversão costuma funcionar bem. Geometria 3D mais
complexa (não é o caso deste pack específico) tende a precisar de ajuste
manual no lado Bedrock.

## Antes de compilar

1. Abra `pom.xml` e ajuste `<paper.version>` para a versão real do seu
   servidor (ex: `1.20.4-R0.1-SNAPSHOT`, `1.21.1-R0.1-SNAPSHOT`). "Purpur
   26.2" não corresponde a uma versão real de Minecraft — Purpur segue a
   numeração do Minecraft (1.20.x, 1.21.x, etc), então confirme qual
   versão de Minecraft seu Purpur realmente é antes de compilar.
2. Display Entities existem a partir do **Minecraft 1.19.4**. Servidores
   mais antigos que isso não conseguem rodar este plugin.
3. Preciso avisar: eu não tenho acesso à internet neste ambiente, então
   não consegui baixar as dependências do Maven para compilar e testar o
   `.jar` aqui. O código foi escrito com cuidado, mas você (ou alguém
   com Java/Maven) vai precisar compilar na sua própria máquina:

   ```
   mvn clean package
   ```

   O `.jar` final aparece em `target/CustomRig.jar`.

## Instalação

1. Coloque `CustomRig.jar` na pasta `plugins/` do servidor.
2. Inicie o servidor uma vez — ele vai criar:
   ```
   plugins/CustomRig/modelos/
   plugins/CustomRig/emotes/
   ```
3. Coloque seus arquivos `.json` de modelo dentro de `modelos/` e seus
   arquivos `.json` de emote dentro de `emotes/`.
4. Rode `/customrigreload` para recarregar sem reiniciar o servidor.

## Comandos

| Comando | Permissão | Descrição |
|---|---|---|
| `/custommodel <jogador> <arquivo>` | `customrig.model` (padrão: op) | Aplica o modelo `arquivo.json` no jogador |
| `/custommodel <jogador> reset` | `customrig.model` | Remove o modelo e volta ao jogador normal |
| `/emotes <nome>` | `customrig.emote` (padrão: todos) | Toca a animação `nome.json` no seu próprio rig |
| `/emotes list` | `customrig.emote` | Lista os emotes disponíveis |
| `/customrigreload` | `customrig.reload` (padrão: op) | Recarrega os arquivos de `modelos/` e `emotes/` |

## Formato do arquivo de modelo (`modelos/nome.json`)

```json
{
  "name": "nome",
  "parts": [
    {
      "id": "head",
      "material": "PLAYER_HEAD",
      "customModelData": 1001,
      "offsetX": 0, "offsetY": 1.5, "offsetZ": 0,
      "scaleX": 1.0, "scaleY": 1.0, "scaleZ": 1.0,
      "followsHeadPitch": true
    }
  ]
}
```

- `material`: qualquer `Material` válido do Bukkit (o item que carrega a
  textura customizada). Recomenda-se `LEATHER_HORSE_ARMOR` ou
  `PLAYER_HEAD` como base, por serem fáceis de re-texturizar.
- `customModelData`: o número que seu resource pack usa para saber qual
  modelo/textura mostrar nesse item.
- `offsetX/Y/Z`: posição da parte relativa aos pés do jogador, em blocos.
- `followsHeadPitch`: se `true`, a parte também rotaciona conforme o
  jogador olha para cima/baixo (use só na cabeça).

Veja `modelos/exemplo.json` como referência funcional (cabeça, tronco,
braços e pernas).

## Formato do arquivo de emote (`emotes/nome.json`)

```json
{
  "name": "nome",
  "durationTicks": 40,
  "keyframes": [
    { "tick": 0, "boneId": "right_arm", "rotX": 0, "rotY": 0, "rotZ": 0, "transX": 0, "transY": 0, "transZ": 0 },
    { "tick": 10, "boneId": "right_arm", "rotX": -90, "rotY": 0, "rotZ": 0, "transX": 0, "transY": 0.3, "transZ": 0 }
  ]
}
```

- `tick`: 20 ticks = 1 segundo.
- `boneId`: precisa bater com o `id` de uma parte do modelo ativo no
  jogador.
- `rotX/Y/Z`: rotação extra (graus) somada à pose base, nos três eixos.
- `transX/Y/Z`: deslocamento extra (blocos) somado à posição base.

Veja `emotes/aceno.json` como referência (levanta o braço e balança).

## Limitações importantes (leia antes de prometer isso aos seus jogadores)

- **Não precisa de mod no cliente** — funciona em Java vanilla e em
  Bedrock via Geyser, porque é tudo baseado em Display Entities e itens
  vanilla com CustomModelData.
- **Precisa de resource pack** para as texturas aparecerem — sem ele,
  os jogadores veem os itens base (ex: uma armadura de cavalo de couro
  comum), não o modelo customizado.
- As rotações usam uma aproximação matemática simples (yaw/pitch do
  jogador + rotação por eixo). Como não pude testar em um servidor real
  aqui (sem acesso à internet neste ambiente para compilar/rodar), é
  possível que algum sinal de rotação precise ser invertido depois de
  testar em jogo — se um braço girar para o lado errado, inverta o sinal
  do `rotZ`/`rotX` correspondente no JSON do emote, é só um ajuste de
  configuração, não estrutural.
- Isso é uma recriação inspirada no Figura/Emotecraft, não os mods
  originais — não há scripting, física de tecido, nem sincronização de
  animações complexas de corpo inteiro além do que os keyframes definem.
