/*
 * Copyright (C) 2022 Patrick Goldinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.patrickgold.florisboard.app.ui.settings.theme

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.app.res.stringRes
import dev.patrickgold.florisboard.app.ui.components.FlorisIconButton
import dev.patrickgold.florisboard.app.ui.components.FlorisOutlinedBox
import dev.patrickgold.florisboard.app.ui.components.FlorisOutlinedTextField
import dev.patrickgold.florisboard.app.ui.components.FlorisScreen
import dev.patrickgold.florisboard.app.ui.components.defaultFlorisOutlinedBox
import dev.patrickgold.florisboard.app.ui.components.florisVerticalScroll
import dev.patrickgold.florisboard.app.ui.components.rippleClickable
import dev.patrickgold.florisboard.app.ui.ext.ExtensionComponentView
import dev.patrickgold.florisboard.common.android.showLongToast
import dev.patrickgold.florisboard.common.rememberValidationResult
import dev.patrickgold.florisboard.ime.theme.FlorisImeUi
import dev.patrickgold.florisboard.ime.theme.FlorisImeUiSpec
import dev.patrickgold.florisboard.ime.theme.ThemeExtensionComponent
import dev.patrickgold.florisboard.ime.theme.ThemeExtensionComponentEditor
import dev.patrickgold.florisboard.ime.theme.ThemeExtensionEditor
import dev.patrickgold.florisboard.res.cache.CacheManager
import dev.patrickgold.florisboard.res.ext.ExtensionValidation
import dev.patrickgold.florisboard.res.io.readJson
import dev.patrickgold.florisboard.res.io.subFile
import dev.patrickgold.florisboard.snygg.Snygg
import dev.patrickgold.florisboard.snygg.SnyggLevel
import dev.patrickgold.florisboard.snygg.SnyggPropertySetEditor
import dev.patrickgold.florisboard.snygg.SnyggPropertySetSpec
import dev.patrickgold.florisboard.snygg.SnyggRule
import dev.patrickgold.florisboard.snygg.SnyggStylesheet
import dev.patrickgold.florisboard.snygg.SnyggStylesheetEditor
import dev.patrickgold.florisboard.snygg.SnyggStylesheetJsonConfig
import dev.patrickgold.florisboard.snygg.definedVariablesRule
import dev.patrickgold.florisboard.snygg.isDefinedVariablesRule
import dev.patrickgold.florisboard.snygg.value.SnyggCutCornerShapeDpValue
import dev.patrickgold.florisboard.snygg.value.SnyggCutCornerShapePercentValue
import dev.patrickgold.florisboard.snygg.value.SnyggDefinedVarValue
import dev.patrickgold.florisboard.snygg.value.SnyggDpSizeValue
import dev.patrickgold.florisboard.snygg.value.SnyggExplicitInheritValue
import dev.patrickgold.florisboard.snygg.value.SnyggImplicitInheritValue
import dev.patrickgold.florisboard.snygg.value.SnyggPercentageSizeValue
import dev.patrickgold.florisboard.snygg.value.SnyggRectangleShapeValue
import dev.patrickgold.florisboard.snygg.value.SnyggRoundedCornerShapeDpValue
import dev.patrickgold.florisboard.snygg.value.SnyggRoundedCornerShapePercentValue
import dev.patrickgold.florisboard.snygg.value.SnyggShapeValue
import dev.patrickgold.florisboard.snygg.value.SnyggSolidColorValue
import dev.patrickgold.florisboard.snygg.value.SnyggSpSizeValue
import dev.patrickgold.florisboard.snygg.value.SnyggValue
import dev.patrickgold.florisboard.snygg.value.SnyggValueEncoder
import dev.patrickgold.jetpref.material.ui.JetPrefAlertDialog
import dev.patrickgold.jetpref.material.ui.JetPrefListItem
import kotlinx.coroutines.launch

internal val IntListSaver = Saver<SnapshotStateList<Int>, ArrayList<Int>>(
    save = { ArrayList(it) },
    restore = { it.toMutableStateList() },
)

@Composable
fun ThemeEditorScreen(
    workspace: CacheManager.ExtEditorWorkspace<*>,
    editor: ThemeExtensionComponentEditor,
) = FlorisScreen {
    title = stringRes(R.string.ext__editor__edit_component__title_theme)
    scrollable = false

    val scope = rememberCoroutineScope()
    val stylesheetEditor = remember {
        editor.stylesheetEditor ?: run {
            val stylesheetPath = editor.stylesheetPath()
            editor.stylesheetPathOnLoad = stylesheetPath
            val stylesheetFile = workspace.dir.subFile(stylesheetPath)
            val stylesheetEditor = if (stylesheetFile.exists()) {
                try {
                    stylesheetFile.readJson<SnyggStylesheet>(SnyggStylesheetJsonConfig).edit()
                } catch (e: Throwable) {
                    SnyggStylesheetEditor()
                }
            } else {
                SnyggStylesheetEditor()
            }
            if (stylesheetEditor.rules.none { (rule, _) -> rule.isDefinedVariablesRule() }) {
                stylesheetEditor.rules[SnyggRule.definedVariablesRule()] = SnyggPropertySetEditor()
            }
            stylesheetEditor
        }.also { editor.stylesheetEditor = it }
    }
    var snyggLevel by rememberSaveable { mutableStateOf(SnyggLevel.ADVANCED) }
    var snyggRuleToEdit by rememberSaveable(saver = SnyggRule.StateSaver) { mutableStateOf(null) }
    var snyggPropertyToEdit by remember { mutableStateOf<PropertyInfo?>(null) }
    var snyggPropertySetForEditing = remember<SnyggPropertySetEditor?> { null }
    var snyggPropertySetSpecForEditing = remember<SnyggPropertySetSpec?> { null }
    var showEditComponentMetaDialog by rememberSaveable { mutableStateOf(false) }

    fun handleBackPress() {
        workspace.currentAction = null
    }

    navigationIcon {
        FlorisIconButton(
            onClick = { handleBackPress() },
            icon = painterResource(R.drawable.ic_close),
        )
    }

    actions {
        FlorisIconButton(
            onClick = {
                snyggLevel = when (snyggLevel) {
                    SnyggLevel.BASIC -> SnyggLevel.ADVANCED
                    SnyggLevel.ADVANCED -> SnyggLevel.DEVELOPER
                    SnyggLevel.DEVELOPER -> SnyggLevel.BASIC
                }
            },
            icon = painterResource(R.drawable.ic_language),
        )
    }

    floatingActionButton {
        ExtendedFloatingActionButton(
            icon = { Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = null,
            ) },
            text = { Text(
                text = stringRes(R.string.settings__theme_editor__add_rule),
            ) },
            onClick = { snyggRuleToEdit = SnyggEmptyRuleForAdding },
        )
    }

    content {
        BackHandler {
            handleBackPress()
        }

        val definedVariables = remember(stylesheetEditor.rules) {
            stylesheetEditor.rules.firstNotNullOfOrNull { (rule, propertySet) ->
                if (rule.isDefinedVariablesRule()) {
                    propertySet.properties
                } else {
                    null
                }
            } ?: emptyMap()
        }

        // TODO: (priority = low)
        //  Floris scrollbar does not like lazy lists with non-constant item heights.
        //  Consider building a custom scrollbar tailored for this list specifically.
        val lazyListState = rememberLazyListState()
        LazyColumn(
            //modifier = Modifier.florisScrollbar(lazyListState, isVertical = true),
            state = lazyListState,
        ) {
            item {
                Column {
                    ExtensionComponentView(
                        modifier = Modifier.defaultFlorisOutlinedBox(),
                        meta = workspace.editor!!.meta,
                        component = editor,
                        onEditBtnClick = { showEditComponentMetaDialog = true },
                    )
                    if (stylesheetEditor.rules.isEmpty() ||
                        (stylesheetEditor.rules.size == 1 && stylesheetEditor.rules.keys.all { it.isDefinedVariablesRule() })
                    ) {
                        Text(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                            text = stringRes(R.string.settings__theme_editor__no_rules_defined),
                            fontStyle = FontStyle.Italic,
                        )
                    }
                }
            }

            items(stylesheetEditor.rules.entries.toList()) { (rule, propertySet) -> key(rule) {
                val isVariablesRule = rule.isDefinedVariablesRule()
                val propertySetSpec = FlorisImeUiSpec.propertySetSpec(rule.element)
                FlorisOutlinedBox(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                        .fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SnyggRuleRow(
                            rule = rule,
                            level = snyggLevel,
                            showEditBtn = !isVariablesRule,
                            onEditRuleBtnClick = {
                                snyggRuleToEdit = rule
                            },
                            onAddPropertyBtnClick = {
                                snyggPropertySetForEditing = propertySet
                                snyggPropertySetSpecForEditing = propertySetSpec
                                snyggPropertyToEdit = SnyggEmptyPropertyInfoForAdding
                            },
                        )
                        if (isVariablesRule) {
                            Text(
                                modifier = Modifier.padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
                                text = stringRes(R.string.snygg__rule_element__defines_description),
                                style = MaterialTheme.typography.body2,
                                fontStyle = FontStyle.Italic,
                            )
                        }
                        for ((propertyName, propertyValue) in propertySet.properties) {
                            val propertySpec = propertySetSpec?.propertySpec(propertyName)
                            if (propertySpec != null && propertySpec.level <= snyggLevel || isVariablesRule) {
                                JetPrefListItem(
                                    modifier = Modifier.rippleClickable {
                                        snyggPropertySetForEditing = propertySet
                                        snyggPropertySetSpecForEditing = propertySetSpec
                                        snyggPropertyToEdit = PropertyInfo(propertyName, propertyValue)
                                    },
                                    text = translatePropertyName(propertyName, snyggLevel),
                                    secondaryText = translatePropertyValue(propertyValue, snyggLevel),
                                    singleLineSecondaryText = true,
                                    trailing = { SnyggValueIcon(propertyValue, definedVariables) },
                                )
                            }
                        }
                    }
                }
            } }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        if (showEditComponentMetaDialog) {
            ComponentMetaEditorDialog(
                workspace = workspace,
                editor = editor,
                onConfirm = { showEditComponentMetaDialog = false },
                onDismiss = { showEditComponentMetaDialog = false },
            )
        }

        val ruleToEdit = snyggRuleToEdit
        if (ruleToEdit != null) {
            EditRuleDialog(
                initRule = ruleToEdit,
                level = snyggLevel,
                onConfirmRule = { oldRule, newRule ->
                    val rules = stylesheetEditor.rules
                    when {
                        oldRule == newRule -> {
                            snyggRuleToEdit = null
                            true
                        }
                        rules.contains(newRule) -> {
                            false
                        }
                        else -> workspace.update {
                            val set = rules.remove(oldRule)
                            when {
                                set != null -> {
                                    rules[newRule] = set
                                    snyggRuleToEdit = null
                                    scope.launch {
                                        lazyListState.animateScrollToItem(index = rules.keys.indexOf(newRule))
                                    }
                                    true
                                }
                                oldRule == SnyggEmptyRuleForAdding -> {
                                    rules[newRule] = SnyggPropertySetEditor()
                                    snyggRuleToEdit = null
                                    scope.launch {
                                        lazyListState.animateScrollToItem(index = rules.keys.indexOf(newRule))
                                    }
                                    true
                                }
                                else -> {
                                    false
                                }
                            }
                        }
                    }
                },
                onDeleteRule = { rule ->
                    workspace.update {
                        stylesheetEditor.rules.remove(rule)
                    }
                    snyggRuleToEdit = null
                },
                onDismiss = { snyggRuleToEdit = null },
            )
        }

        val propertyToEdit = snyggPropertyToEdit
        if (propertyToEdit != null) {
            EditPropertyDialog(
                propertySetSpec = snyggPropertySetSpecForEditing,
                initProperty = propertyToEdit,
                level = snyggLevel,
                definedVariables = definedVariables,
                onConfirmNewValue = { name, value ->
                    val properties = snyggPropertySetForEditing?.properties ?: return@EditPropertyDialog false
                    if (propertyToEdit == SnyggEmptyPropertyInfoForAdding && properties.containsKey(name)) {
                        return@EditPropertyDialog false
                    }
                    workspace.update {
                        properties[name] = value
                    }
                    snyggPropertyToEdit = null
                    true
                },
                onDelete = {
                    workspace.update {
                        snyggPropertySetForEditing?.properties?.remove(propertyToEdit.name)
                    }
                    snyggPropertyToEdit = null
                },
                onDismiss = { snyggPropertyToEdit = null },
            )
        }
    }
}

@Composable
private fun ComponentMetaEditorDialog(
    workspace: CacheManager.ExtEditorWorkspace<*>,
    editor: ThemeExtensionComponentEditor,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var showValidationErrors by rememberSaveable { mutableStateOf(false) }

    var id by rememberSaveable { mutableStateOf(editor.id) }
    val idValidation = rememberValidationResult(ExtensionValidation.ComponentId, id)
    var label by rememberSaveable { mutableStateOf(editor.label) }
    val labelValidation = rememberValidationResult(ExtensionValidation.ComponentLabel, label)
    var authors by rememberSaveable { mutableStateOf(editor.authors.joinToString("\n")) }
    val authorsValidation = rememberValidationResult(ExtensionValidation.ComponentAuthors, authors)
    var isNightTheme by rememberSaveable { mutableStateOf(editor.isNightTheme) }
    var isBorderless by rememberSaveable { mutableStateOf(editor.isBorderless) }
    val isMaterialYouAware by rememberSaveable { mutableStateOf(editor.isMaterialYouAware) }
    var stylesheetPath by rememberSaveable { mutableStateOf(editor.stylesheetPath) }
    val stylesheetPathValidation = rememberValidationResult(ExtensionValidation.ThemeComponentStylesheetPath, stylesheetPath)

    JetPrefAlertDialog(
        title = stringRes(R.string.ext__editor__metadata__title),
        confirmLabel = stringRes(R.string.action__apply),
        onConfirm = {
            val allFieldsValid = idValidation.isValid() &&
                labelValidation.isValid() &&
                authorsValidation.isValid() &&
                stylesheetPathValidation.isValid()
            if (!allFieldsValid) {
                showValidationErrors = true
            } else if (id != editor.id && (workspace.editor as? ThemeExtensionEditor)?.themes?.find { it.id == id.trim() } != null) {
                context.showLongToast("A theme with this ID already exists!")
            } else {
                workspace.update {
                    editor.id = id.trim()
                    editor.label = label.trim()
                    editor.authors = authors.lines().map { it.trim() }.filter { it.isNotBlank() }
                    editor.isNightTheme = isNightTheme
                    editor.isBorderless = isBorderless
                    editor.isMaterialYouAware = isMaterialYouAware
                    editor.stylesheetPath = stylesheetPath.trim()
                }
                onConfirm()
            }
        },
        dismissLabel = stringRes(R.string.action__cancel),
        onDismiss = onDismiss,
        scrollModifier = Modifier.florisVerticalScroll(),
    ) {
        Column {
            DialogProperty(text = stringRes(R.string.ext__meta__id)) {
                FlorisOutlinedTextField(
                    value = id,
                    onValueChange = { id = it },
                    singleLine = true,
                    showValidationError = showValidationErrors,
                    validationResult = idValidation,
                )
            }
            DialogProperty(text = stringRes(R.string.ext__meta__label)) {
                FlorisOutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    singleLine = true,
                    showValidationError = showValidationErrors,
                    validationResult = labelValidation,
                )
            }
            DialogProperty(text = stringRes(R.string.ext__meta__authors)) {
                FlorisOutlinedTextField(
                    value = authors,
                    onValueChange = { authors = it },
                    showValidationError = showValidationErrors,
                    validationResult = authorsValidation,
                )
            }
            JetPrefListItem(
                modifier = Modifier.toggleable(isNightTheme) { isNightTheme = it },
                text = stringRes(R.string.settings__theme_editor__component_meta_is_night_theme),
                trailing = {
                    Switch(checked = isNightTheme, onCheckedChange = null)
                },
            )
            JetPrefListItem(
                modifier = Modifier.toggleable(isBorderless) { isBorderless = it },
                text = stringRes(R.string.settings__theme_editor__component_meta_is_borderless),
                trailing = {
                    Switch(checked = isBorderless, onCheckedChange = null)
                },
            )
            DialogProperty(text = stringRes(R.string.settings__theme_editor__component_meta_stylesheet_path)) {
                FlorisOutlinedTextField(
                    value = stylesheetPath,
                    onValueChange = { stylesheetPath = it },
                    singleLine = true,
                    placeholder = if (stylesheetPath.isEmpty()) {
                        ThemeExtensionComponent.defaultStylesheetPath(id.trim())
                    } else {
                        null
                    },
                    showValidationError = showValidationErrors,
                    validationResult = stylesheetPathValidation,
                )
            }
        }
    }
}

@Composable
private fun SnyggRuleRow(
    rule: SnyggRule,
    level: SnyggLevel,
    showEditBtn: Boolean,
    onEditRuleBtnClick: () -> Unit,
    onAddPropertyBtnClick: () -> Unit,
) {
    @Composable
    fun Selector(text: String) {
        Text(
            modifier = Modifier
                .padding(end = 8.dp)
                .background(MaterialTheme.colors.primaryVariant),
            text = text,
            style = MaterialTheme.typography.body2,
            fontFamily = FontFamily.Monospace,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }

    @Composable
    fun AttributesList(text: String, list: String) {
        Text(
            text = "$text = $list",
            style = MaterialTheme.typography.body2,
            color = LocalContentColor.current.copy(alpha = 0.56f),
            fontFamily = FontFamily.Monospace,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp, horizontal = 10.dp),
        ) {
            Text(
                text = translateElementName(rule, level),
                style = MaterialTheme.typography.body2,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                if (rule.pressedSelector) {
                    Selector(text = when (level) {
                        SnyggLevel.DEVELOPER -> SnyggRule.PRESSED_SELECTOR
                        else -> stringRes(R.string.snygg__rule_selector__pressed)
                    })
                }
                if (rule.focusSelector) {
                    Selector(text = when (level) {
                        SnyggLevel.DEVELOPER -> SnyggRule.FOCUS_SELECTOR
                        else -> stringRes(R.string.snygg__rule_selector__focus)
                    })
                }
                if (rule.disabledSelector) {
                    Selector(text = when (level) {
                        SnyggLevel.DEVELOPER -> SnyggRule.DISABLED_SELECTOR
                        else -> stringRes(R.string.snygg__rule_selector__disabled)
                    })
                }
            }
            if (rule.codes.isNotEmpty()) {
                AttributesList(text = "codes", list = remember(rule.codes) { rule.codes.toString() })
            }
            if (rule.modes.isNotEmpty()) {
                AttributesList(text = "modes", list = remember(rule.modes) { rule.modes.toString() })
            }
        }
        if (showEditBtn) {
            FlorisIconButton(
                onClick = onEditRuleBtnClick,
                icon = painterResource(R.drawable.ic_edit),
                iconColor = MaterialTheme.colors.primary,
                iconModifier = Modifier.size(ButtonDefaults.IconSize),
            )
        }
        FlorisIconButton(
            onClick = onAddPropertyBtnClick,
            icon = painterResource(R.drawable.ic_add),
            iconColor = MaterialTheme.colors.secondary,
            iconModifier = Modifier.size(ButtonDefaults.IconSize),
        )
    }
}

@Composable
internal fun DialogProperty(
    text: String,
    modifier: Modifier = Modifier,
    trailingIconTitle: @Composable () -> Unit = { },
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.padding(bottom = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                text = text,
                style = MaterialTheme.typography.subtitle2,
            )
            trailingIconTitle()
        }
        content()
    }
}

object SnyggValueIcon {
    interface Spec {
        val borderWith: Dp
        val boxShape: Shape
        val elevation: Dp
        val iconSize: Dp
        val iconSizeMinusBorder: Dp
    }

    object Small : Spec {
        override val borderWith = Dp.Hairline
        override val boxShape = RoundedCornerShape(4.dp)
        override val elevation = 4.dp
        override val iconSize = 16.dp
        override val iconSizeMinusBorder = 16.dp
    }

    object Normal : Spec {
        override val borderWith = 1.dp
        override val boxShape = RoundedCornerShape(8.dp)
        override val elevation = 4.dp
        override val iconSize = 24.dp
        override val iconSizeMinusBorder = 22.dp
    }
}

@Composable
internal fun SnyggValueIcon(
    value: SnyggValue,
    definedVariables: Map<String, SnyggValue>,
    modifier: Modifier = Modifier,
    spec: SnyggValueIcon.Spec = SnyggValueIcon.Normal,
) {
    when (value) {
        is SnyggSolidColorValue -> {
            Surface(
                modifier = modifier.requiredSize(spec.iconSize),
                color = MaterialTheme.colors.background,
                elevation = spec.elevation,
                shape = spec.boxShape,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(value.color),
                )
            }
        }
        is SnyggShapeValue -> {
            Box(
                modifier = modifier
                    .requiredSize(spec.iconSizeMinusBorder)
                    .border(spec.borderWith, MaterialTheme.colors.onBackground, value.shape)
            )
        }
        is SnyggSpSizeValue -> {
            Icon(
                modifier = modifier.requiredSize(spec.iconSize),
                painter = painterResource(R.drawable.ic_format_size),
                contentDescription = null,
            )
        }
        is SnyggDefinedVarValue -> {
            val realValue = definedVariables[value.key]
            if (realValue == null) {
                Icon(
                    modifier = modifier.requiredSize(spec.iconSize),
                    painter = painterResource(R.drawable.ic_link),
                    contentDescription = null,
                )
            } else {
                val smallSpec = SnyggValueIcon.Small
                Box(modifier = modifier
                    .requiredSize(spec.iconSize)
                    .offset(y = (-2).dp)) {
                    SnyggValueIcon(
                        modifier = Modifier.offset(x = 8.dp, y = 8.dp),
                        value = realValue,
                        definedVariables = definedVariables,
                        spec = smallSpec,
                    )
                    Box(
                        modifier = Modifier
                            .offset(x = 1.dp)
                            .requiredSize(smallSpec.iconSize)
                            .padding(vertical = 2.dp)
                            .background(MaterialTheme.colors.background, spec.boxShape),
                    )
                    Icon(
                        modifier = Modifier.requiredSize(smallSpec.iconSize),
                        painter = painterResource(R.drawable.ic_link),
                        contentDescription = null,
                    )
                }
            }
        }
        else -> {
            // Render nothing
        }
    }
}

@Composable
internal fun translateElementName(rule: SnyggRule, level: SnyggLevel): String {
    return translateElementName(rule.element, level) ?: remember {
        buildString {
            if (rule.isAnnotation) {
                append(SnyggRule.ANNOTATION_MARKER)
            }
            append(rule.element)
        }
    }
}

@Composable
internal fun translateElementName(element: String, level: SnyggLevel): String? {
    return when (level) {
        SnyggLevel.DEVELOPER -> null
        else -> when (element) {
            "defines" -> R.string.snygg__rule_element__defines
            FlorisImeUi.Keyboard -> R.string.snygg__rule_element__keyboard
            FlorisImeUi.Key -> R.string.snygg__rule_element__key
            FlorisImeUi.KeyHint -> R.string.snygg__rule_element__key_hint
            FlorisImeUi.KeyPopup -> R.string.snygg__rule_element__key_popup
            FlorisImeUi.ClipboardHeader -> R.string.snygg__rule_element__clipboard_header
            FlorisImeUi.ClipboardItem -> R.string.snygg__rule_element__clipboard_item
            FlorisImeUi.ClipboardItemPopup -> R.string.snygg__rule_element__clipboard_item_popup
            FlorisImeUi.OneHandedPanel -> R.string.snygg__rule_element__one_handed_panel
            FlorisImeUi.SmartbarPrimaryRow -> R.string.snygg__rule_element__smartbar_primary_row
            FlorisImeUi.SmartbarPrimaryActionRowToggle -> R.string.snygg__rule_element__smartbar_primary_action_row_toggle
            FlorisImeUi.SmartbarPrimarySecondaryRowToggle -> R.string.snygg__rule_element__smartbar_primary_secondary_row_toggle
            FlorisImeUi.SmartbarSecondaryRow -> R.string.snygg__rule_element__smartbar_secondary_row
            FlorisImeUi.SmartbarActionRow -> R.string.snygg__rule_element__smartbar_action_row
            FlorisImeUi.SmartbarActionButton -> R.string.snygg__rule_element__smartbar_action_button
            FlorisImeUi.SmartbarCandidateRow -> R.string.snygg__rule_element__smartbar_candidate_row
            FlorisImeUi.SmartbarCandidateWord -> R.string.snygg__rule_element__smartbar_candidate_word
            FlorisImeUi.SmartbarCandidateClip -> R.string.snygg__rule_element__smartbar_candidate_clip
            FlorisImeUi.SmartbarCandidateSpacer -> R.string.snygg__rule_element__smartbar_candidate_spacer
            FlorisImeUi.SmartbarKey -> R.string.snygg__rule_element__smartbar_key
            FlorisImeUi.SystemNavBar -> R.string.snygg__rule_element__system_nav_bar
            else -> null
        }
    }.let { if (it != null) { stringRes(it) } else { null } }
}

@Composable
internal fun translatePropertyName(propertyName: String, level: SnyggLevel): String {
    return when (level) {
        SnyggLevel.DEVELOPER -> null
        else -> when (propertyName) {
            Snygg.Width -> R.string.snygg__property_name__width
            Snygg.Height -> R.string.snygg__property_name__height
            Snygg.Background -> R.string.snygg__property_name__background
            Snygg.Foreground -> R.string.snygg__property_name__foreground
            Snygg.Border -> R.string.snygg__property_name__border
            Snygg.BorderTop -> R.string.snygg__property_name__border_top
            Snygg.BorderBottom -> R.string.snygg__property_name__border_bottom
            Snygg.BorderStart -> R.string.snygg__property_name__border_start
            Snygg.BorderEnd -> R.string.snygg__property_name__border_end
            Snygg.FontFamily -> R.string.snygg__property_name__font_family
            Snygg.FontSize -> R.string.snygg__property_name__font_size
            Snygg.FontStyle -> R.string.snygg__property_name__font_style
            Snygg.FontVariant -> R.string.snygg__property_name__font_variant
            Snygg.FontWeight -> R.string.snygg__property_name__font_weight
            Snygg.Shadow -> R.string.snygg__property_name__shadow
            Snygg.Shape -> R.string.snygg__property_name__shape
            "--primary" -> R.string.snygg__property_name__var_primary
            "--primary-variant" -> R.string.snygg__property_name__var_primary_variant
            "--secondary" -> R.string.snygg__property_name__var_secondary
            "--secondary-variant" -> R.string.snygg__property_name__var_secondary_variant
            "--background" -> R.string.snygg__property_name__var_background
            "--surface" -> R.string.snygg__property_name__var_surface
            "--surface-variant" -> R.string.snygg__property_name__var_surface_variant
            "--on-primary" -> R.string.snygg__property_name__var_on_primary
            "--on-secondary" -> R.string.snygg__property_name__var_on_secondary
            "--on-background" -> R.string.snygg__property_name__var_on_background
            "--on-surface" -> R.string.snygg__property_name__var_on_surface
            else -> null
        }
    }.let { resId ->
        when {
            resId != null -> {
                stringRes(resId)
            }
            propertyName.isBlank() -> {
                stringRes(R.string.general__select_dropdown_value_placeholder)
            }
            else -> {
                propertyName
            }
        }
    }
}

@Composable
internal fun translatePropertyValue(propertyValue: SnyggValue, level: SnyggLevel): String {
    return propertyValue.encoder().serialize(propertyValue).getOrElse { propertyValue.toString() }
}

@Composable
internal fun translatePropertyValueEncoderName(encoder: SnyggValueEncoder): String {
    return when (encoder) {
        SnyggImplicitInheritValue -> R.string.general__select_dropdown_value_placeholder
        SnyggExplicitInheritValue -> R.string.snygg__property_value__explicit_inherit
        SnyggSolidColorValue -> R.string.snygg__property_value__solid_color
        SnyggRectangleShapeValue -> R.string.snygg__property_value__rectangle_shape
        SnyggCutCornerShapeDpValue -> R.string.snygg__property_value__cut_corner_shape_dp
        SnyggCutCornerShapePercentValue -> R.string.snygg__property_value__cut_corner_shape_percent
        SnyggRoundedCornerShapeDpValue -> R.string.snygg__property_value__rounded_corner_shape_dp
        SnyggRoundedCornerShapePercentValue -> R.string.snygg__property_value__rounded_corner_shape_percent
        SnyggDpSizeValue -> R.string.snygg__property_value__dp_size
        SnyggSpSizeValue -> R.string.snygg__property_value__sp_size
        SnyggPercentageSizeValue -> R.string.snygg__property_value__percentage_size
        SnyggDefinedVarValue -> R.string.snygg__property_value__defined_var
        else -> null
    }.let { if (it != null) { stringRes(it) } else { encoder::class.simpleName ?: "" } }.toString()
}
