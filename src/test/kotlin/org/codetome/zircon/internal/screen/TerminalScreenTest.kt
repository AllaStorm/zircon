package org.codetome.zircon.internal.screen

import org.assertj.core.api.Assertions.assertThat
import org.codetome.zircon.api.Position
import org.codetome.zircon.api.Size
import org.codetome.zircon.api.builder.TextCharacterBuilder
import org.codetome.zircon.api.resource.CP437TilesetResource
import org.codetome.zircon.internal.terminal.virtual.VirtualTerminal
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class TerminalScreenTest {

    lateinit var target: TerminalScreen
    val terminal = VirtualTerminal(
            initialSize = SIZE,
            initialFont = FONT)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        target = TerminalScreen(terminal)
    }

    @Test
    fun shouldBeAbleToPutCharacterWhenPutCharacterIsCalled() {
        val char = 'x'
        val expected = TextCharacterBuilder.newBuilder()
                .styleSet(target.toStyleSet())
                .character(char)
                .build()
        val currCursorPos = target.getCursorPosition()

        target.putCharacter(char)

        assertThat(target.getCharacterAt(currCursorPos).get()).isEqualTo(expected)
        assertThat(target.getCursorPosition()).isEqualTo(currCursorPos.withRelativeColumn(1))

    }

    @Test
    fun shouldUseTerminalsFontWhenCreating() {
        assertThat(target.getCurrentFont().getId())
                .isEqualTo(terminal.getCurrentFont().getId())
    }

    @Test
    fun shouldProperlyOverrideTerminalFontWhenHasOverrideFontAndDisplayIsCalled() {
        val expectedFont = CP437TilesetResource.AESOMATICA_16X16.toFont()
        target.useFont(expectedFont)
        target.display()
        assertThat(target.getCurrentFont().getId()).isEqualTo(expectedFont.getId())
        assertThat(terminal.getCurrentFont().getId()).isEqualTo(expectedFont.getId())
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldProperlyThrowExceptionWhenTyringToSetNonCompatibleFont() {
        target.useFont(CP437TilesetResource.BISASAM_20X20.toFont())
    }

    @Test
    fun shouldResizeOnResize() {
        val expectedSize = Size(5, 5)
        terminal.setSize(expectedSize)
        assertThat(terminal.getBoundableSize()).isEqualTo(expectedSize)
    }


    @Test
    fun shouldBeDrawnWhenCharacterSet() {
        target.setCharacterAt(Position.OFFSET_1x1, CHAR)
        assertThat(target.getCharacterAt(Position.OFFSET_1x1).get())
                .isEqualTo(CHAR)

    }

    @Test
    fun shouldClearProperlyWhenClearIsCalled() {
        target.setCharacterAt(Position.OFFSET_1x1, CHAR)
        target.display()

        target.clear()

        assertThat(target.getCharacterAt(Position.OFFSET_1x1))
                .isNotEqualTo(CHAR)
    }


    companion object {
        val SIZE = Size(10, 10)
        val FONT = CP437TilesetResource.ROGUE_YUN_16X16.toFont()
        val CHAR = TextCharacterBuilder.newBuilder()
                .character('x')
                .build()
    }
}