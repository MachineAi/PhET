/* ====================================================================
 * Copyright (c) 2001-2003 OYOAHA. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. The names "OYOAHA" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. 
 *    For written permission, please contact email@oyoaha.com.
 *
 * 3. Products derived from this software may not be called "OYOAHA",
 *    nor may "OYOAHA" appear in their name, without prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL OYOAHA OR ITS CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oyoaha.swing.plaf.oyoaha.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaListUI extends BasicListUI
{
  public static ComponentUI createUI(JComponent c)
  {
    return new OyoahaListUI();
  }

  public void update(Graphics g, JComponent c)
  {
      OyoahaUtilities.paintBackground(g,c);
      paint(g,c);
  }

  protected void installDefaults()
  {
    list.setLayout(null);
    LookAndFeel.installBorder(list, "List.border");
    LookAndFeel.installColorsAndFont(list, "List.background", "List.foreground", "List.font");

    if (list.getCellRenderer() == null)
    {
      list.setCellRenderer((ListCellRenderer)(UIManager.get("List.cellRenderer")));
    }

    Color sbg = list.getSelectionBackground();
    if (sbg == null || sbg instanceof UIResource)
    {
      list.setSelectionBackground(UIManager.getColor("List.selectionBackground"));
    }

    Color sfg = list.getSelectionForeground();

    if (sfg == null || sfg instanceof UIResource)
    {
      list.setSelectionForeground(UIManager.getColor("List.selectionForeground"));
    }
  }
}