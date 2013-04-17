JCOD: A Java port of the libTCOD Library for Roguelikes

No detailed writeup yet, just some bullet points.

IMPLEMENTED

* Console class with drawing functions for cells, text, rects, lines, and
  offscreen blit.  Limited image blit support is also available.

* LibGDX scene2d widget rendering console to screen.

* Partial port of TCOD's samples app

STILL TO DO

* TCOD APIs: BSP, FOV, Heightmap, Pathfinding

* Scrollable console viewport(s) in display widget

* Richer mapping between console and display (e.g. mouse/key event routing)

* Support for arbitrary layers in console and display

* Borders, fringes, and splatting

* Animated tiles and tile transitions

* Isometric tiles

Compatibility notes:

* Colors are 32-bit RGBA, using LibGDX's Color class, which works quite
  differently than TCODColor.  For one, color components are floats, not ints.
  They're also mutable and passed by reference.  API functions won't hold on to
  references you pass in, but getters may return refs directly.  This asymmetry is
  all about minimizing garbage, which can be a problem on platforms like Android.

* The "background flag" enums have been replaced by first-class blending function
  objects, and stock BlendMode enums that work essentially the same.  Read the
  BlendMode javadoc for more info.

* Image functions use GDX Pixmap objects, so only a couple functions are supported
 (blit and blit2x).  Key colors are not supported -- use alpha instead.  Mipmaps
 are not supported and probably never will be.  Use of LibGDX is highly
 recommended instead of JCOD image functions for most uses.

* A couple noise functions are supported via a third party implementation, but I'll
  likely either replace them with ports of the TCOD functions or remove them
  entirely (any generic noise implementation should work).

* Not in JCOD:
  + Containers - Java has perfectly good collections.
  + Compression functions - ditto.
  + Random numbers - java.util.Random is good enough.  MT can be had third-party.
  + Filesystem functions - This includes the config format parser.
  + Name generation - come on, really?

