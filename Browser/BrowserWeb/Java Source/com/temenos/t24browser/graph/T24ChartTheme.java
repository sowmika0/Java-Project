package com.temenos.t24browser.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.PeriodAxis;
import org.jfree.chart.axis.PeriodAxisLabelInfo;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.Block;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.LabelBlock;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.CombinedRangeCategoryPlot;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.BarPainter;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineRenderer3D;
import org.jfree.chart.renderer.category.MinMaxCategoryRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.CompositeTitle;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.PublicCloneable;

/**
 * A T24 implementation of the {@link ChartTheme} interface, based on the (@link StandardChartTheme).
 * This implementation just collects a whole bunch of chart attributes and mimics
 * the manual process of applying each attribute to the right sub-object
 * within the JFreeChart instance.  It's not elegant code, but it works.
 */
public class T24ChartTheme implements ChartTheme {

    /** * The largest font size.  Use for the main chart title. */
    private Font extraLargeFont;

    /** A large font.  Used for subtitles. */
    private Font largeFont;

    /** The regular font size.  Used for axis tick labels, legend items etc. */
    private Font regularFont;

    /** The small font size. */
    private Font smallFont;

    /** The paint used to display the main chart title. */
    private Paint titlePaint;

    /** The paint used to display subtitles. */
    private Paint subtitlePaint;

    /** The background paint for the chart. */
    private Paint chartBackgroundPaint;

    /** The legend background paint. */
    private Paint legendBackgroundPaint;

    /** The legend item paint. */
    private Paint legendItemPaint;

    /** The drawing supplier. */
    private DrawingSupplier drawingSupplier;

    /** The background paint for the plot. */
    private Paint plotBackgroundPaint;

    /** The plot outline paint. */
    private Paint plotOutlinePaint;

    /** The label link style for pie charts. */
    private PieLabelLinkStyle labelLinkStyle;

    /** The label link paint for pie charts. */
    private Paint labelLinkPaint;

    /** The domain grid line paint. */
    private Paint domainGridlinePaint;

    /** The range grid line paint. */
    private Paint rangeGridlinePaint;

    /** The crosshair paint. */
    private Paint crosshairPaint;

    /** The axis offsets. */
    private RectangleInsets axisOffset;

    /** The axis label paint. */
    private Paint axisLabelPaint;

    /** The tick label paint. */
    private Paint tickLabelPaint;

    /** The item label paint. */
    private Paint itemLabelPaint;

    /** A flag that controls whether or not shadows are visible (for example, in a bar renderer). */
    private boolean shadowVisible;
    
    /** A flag that controls whether or not outlines are visible (for example, in a bar renderer). */
	private boolean outlineVisible;

    /** The shadow paint. */
    private Paint shadowPaint;

    /** The bar painter. */
    private BarPainter barPainter;

    /** The XY bar painter. */
    private XYBarPainter xyBarPainter;

    /** The thermometer paint. */
    private Paint thermometerPaint;

    /** The paint used to fill the interior of the 'walls' in the background
     * of a plot with a 3D effect.  Applied to BarRenderer3D. */
    private Paint wallPaint;

    /** The error indicator paint for the {@link StatisticalBarRenderer}. */
    private Paint errorIndicatorPaint;

    /** The grid band paint for a {@link SymbolAxis}. */
    private Paint gridBandPaint = SymbolAxis.DEFAULT_GRID_BAND_PAINT;

    /** The grid band alternate paint for a {@link SymbolAxis}. */
    private Paint gridBandAlternatePaint = SymbolAxis.DEFAULT_GRID_BAND_ALTERNATE_PAINT;

    /** The colour to use for pie graph shadows. */
	private Paint pieShadowPaint;

	/** Colours for the lines / pie segments */
	private static final Color[] COLOUR_SERIES = {
		Color.decode("0x003399"),
		Color.decode("0xCBD5E2"),
		Color.decode("0x7599B5"),
		Color.decode("0xD4D4D4"),
		  		
		Color.decode("0xFFD007"),
		Color.decode("0x949CA4"),
		Color.decode("0xFFE9C5"),
		Color.decode("0xE2AB52"),
		  
		Color.decode("0xBFB095"),
		Color.decode("0xFF9423"),
		Color.decode("0xDB0D1A"),
		Color.decode("0xA10023"),
		  
		Color.decode("0x757BCA"),
		Color.decode("0xBEA4AA"),	
		Color.decode("0x50202D"),
		Color.decode("0x6C7582"),
		  
		Color.decode("0xCDD6CA"),
		Color.decode("0x9EAF9E"),
		Color.decode("0x386A57"),
		Color.decode("0x93AEE6"),
		  
		Color.decode("0x306CC4"),
		Color.decode("0x7F6D42"),
		Color.decode("0xEAE1CD"),
		Color.decode("0x356B99"),
		    		
		Color.decode("0xAAABDE"),
		Color.decode("0x383683"),
		Color.decode("0xBBB0DC"),
		Color.decode("0x8571BE"),
		
		Color.decode("0x4C1384"),
		Color.decode("0x917374"),
		Color.decode("0xDDD2CC"),
		Color.decode("0x8C4B00"),
	 
		Color.decode("0xFFD591"),
		Color.decode("0xCC6A0B"),
		Color.decode("0xFFF57B"),
		Color.decode("0xCEA700"),
		  
			/** repeat colours */
		Color.decode("0xCBD5E2"),
		Color.decode("0x7599B5"),
		Color.decode("0xD4D4D4"),
		  		
		Color.decode("0xFFD007"),
		Color.decode("0x949CA4"),
		Color.decode("0xFFE9C5"),
		Color.decode("0xE2AB52"),
		  
		Color.decode("0xBFB095"),
		Color.decode("0xFF9423"),
		Color.decode("0xDB0D1A"),
		Color.decode("0xA10023"),
		  
		Color.decode("0x757BCA"),
		Color.decode("0xBEA4AA"),	
		Color.decode("0x50202D"),
	};

	/** Colours for the graph */
	static final Color COLOUR_BACKGROUND = new Color(239,243,255,127);

    /**
     * Creates a new default instance.
     */
    public T24ChartTheme() {

        // Set the background colour
        this.chartBackgroundPaint = Color.white;
        this.plotBackgroundPaint = Color.white;

        // Set up the drawing supplier
        Paint[] paintSequence = COLOUR_SERIES;	// Set the colours of the series.
        Shape[] dots = new Shape[] { new Ellipse2D.Double(-3.0,-3.0,6.0,6.0)};	// Ensures that all series use a circle to display points.
        this.drawingSupplier = new DefaultDrawingSupplier(
                paintSequence,
                new Paint[] { Color.decode("0xFFFF00"),
                        Color.decode("0x0036CC")},
                new Stroke[] { new BasicStroke(2.0f)},
                new Stroke[] { new BasicStroke(0.5f)},
                dots);

        // Categorical chart styles (bar and line graphs)
        this.rangeGridlinePaint = Color.black;
        
        // Bar chart styles
        this.barPainter = new StandardBarPainter();		// Use a simple filled colour for the bar, not the default 'reflection' look
        this.shadowVisible = false;						// Get rid of the shadow behind the bar
        this.outlineVisible = true;						// Add an outline to bars

        // Pie chart styles
        this.pieShadowPaint = new Color(0,0,0,0);		// Transparent white colour, so no shadow is displayed.
        
        // Title and legend styles
        this.extraLargeFont = new Font("Arial", Font.BOLD, 14);
        this.largeFont = new Font("Arial", Font.BOLD, 12);
        this.regularFont = new Font("Arial", Font.PLAIN, 10);
        this.smallFont = new Font("Arial", Font.PLAIN, 8);
        this.titlePaint = Color.decode("0x003399");
        this.legendItemPaint = Color.decode("0x003399");
        this.legendBackgroundPaint = Color.white;

        // Default values, not modified from the StandardChartTheme
    	this.subtitlePaint = Color.black;
        this.shadowPaint = Color.gray;
        this.xyBarPainter = new StandardXYBarPainter();
        this.itemLabelPaint = Color.black;
        this.thermometerPaint = Color.white;
        this.wallPaint = BarRenderer3D.DEFAULT_WALL_PAINT;
        this.errorIndicatorPaint = Color.black;
        this.plotOutlinePaint = Color.black;
        this.labelLinkPaint = Color.black;
        this.labelLinkStyle = PieLabelLinkStyle.CUBIC_CURVE;
        this.axisOffset = new RectangleInsets(4, 4, 4, 4);
        this.domainGridlinePaint = Color.white;
        this.crosshairPaint = Color.blue;
        this.axisLabelPaint = Color.darkGray;
        this.tickLabelPaint = Color.darkGray;
    }

    /**
     * Returns the largest font for this theme.
     * @return The largest font for this theme.
     * @see #setExtraLargeFont(Font)
     */
    public Font getExtraLargeFont() {
        return this.extraLargeFont;
    }

    /**
     * Sets the largest font for this theme.
     * @param font  the font (<code>null</code> not permitted).
     * @see #getExtraLargeFont()
     */
    public void setExtraLargeFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.extraLargeFont = font;
    }



    /**
     * Sets the large font for this theme.
     * @param font  the font (<code>null</code> not permitted).
     * @see #getLargeFont()
     */
    public void setLargeFont(Font font) {
        this.largeFont = font;
    }

    /**
     * Returns the regular font.
     * @return The regular font (never <code>null</code>).
     * @see #setRegularFont(Font)
     */
    public Font getRegularFont() {
        return this.regularFont;
    }

    /**
     * Sets the regular font for this theme.
     * @param font  the font (<code>null</code> not permitted).
     * @see #getRegularFont()
     */
    public void setRegularFont(Font font) {
        this.regularFont = font;
    }

    /**
     * Returns the title paint.
     * @return The title paint (never <code>null</code>).
     * @see #setTitlePaint(Paint)
     */
    public Paint getTitlePaint() {
        return this.titlePaint;
    }

    /**
     * Sets the title paint.
     * @param paint  the paint (<code>null</code> not permitted).
     * @see #getTitlePaint()
     */
    public void setTitlePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.titlePaint = paint;
    }

    /**
     * Returns the subtitle paint.
     * @return The subtitle paint (never <code>null</code>).
     * @see #setSubtitlePaint(Paint)
     */
    public Paint getSubtitlePaint() {
        return this.subtitlePaint;
    }

    /**
     * Sets the subtitle paint.
     * @param paint  the paint (<code>null</code> not permitted).
     * @see #getSubtitlePaint()
     */
    public void setSubtitlePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.subtitlePaint = paint;
    }

    /**
     * Returns the chart background paint.
     * @return The chart background paint (never <code>null</code>).
     * @see #setChartBackgroundPaint(Paint)
     */
    public Paint getChartBackgroundPaint() {
        return this.chartBackgroundPaint;
    }

    /**
     * Sets the chart background paint.
     * @param paint  the paint (<code>null</code> not permitted).
     * @see #getChartBackgroundPaint()
     */
    public void setChartBackgroundPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.chartBackgroundPaint = paint;
    }

    /**
     * Returns the legend background paint.
     * @return The legend background paint (never <code>null</code>).
     * @see #setLegendBackgroundPaint(Paint)
     */
    public Paint getLegendBackgroundPaint() {
        return this.legendBackgroundPaint;
    }

    /**
     * Sets the legend background paint.
     * @param paint  the paint (<code>null</code> not permitted).
     * @see #getLegendBackgroundPaint()
     */
    public void setLegendBackgroundPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.legendBackgroundPaint = paint;
    }

    /**
     * Returns the legend item paint.
     * @return The legend item paint (never <code>null</code>).
     * @see #setLegendItemPaint(Paint)
     */
    public Paint getLegendItemPaint() {
        return this.legendItemPaint;
    }

    /**
     * Sets the legend item paint.
     * @param paint  the paint (<code>null</code> not permitted).
     * @see #getLegendItemPaint()
     */
    public void setLegendItemPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.legendItemPaint = paint;
    }

    /**
     * Returns the plot background paint.
     * @return The plot background paint (never <code>null</code>).
     * @see #setPlotBackgroundPaint(Paint)
     */
    public Paint getPlotBackgroundPaint() {
        return this.plotBackgroundPaint;
    }

    /**
     * Sets the plot background paint.
     * @param paint  the paint (<code>null</code> not permitted).
     * @see #getPlotBackgroundPaint()
     */
    public void setPlotBackgroundPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.plotBackgroundPaint = paint;
    }

    /**
     * Returns the plot outline paint.
     * @return The plot outline paint (never <code>null</code>).
     * @see #setPlotOutlinePaint(Paint)
     */
    public Paint getPlotOutlinePaint() {
        return this.plotOutlinePaint;
    }

    /**
     * Sets the plot outline paint.
     * @param paint  the paint (<code>null</code> not permitted).
     * @see #getPlotOutlinePaint()
     */
    public void setPlotOutlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.plotOutlinePaint = paint;
    }

    /**
     * Returns the label link style for pie charts.
     * @return The label link style (never <code>null</code>).
     * @see #setLabelLinkStyle(PieLabelLinkStyle)
     */
    public PieLabelLinkStyle getLabelLinkStyle() {
        return this.labelLinkStyle;
    }

    /**
     * Sets the label link style for pie charts.
     * @param style  the style (<code>null</code> not permitted).
     * @see #getLabelLinkStyle()
     */
    public void setLabelLinkStyle(PieLabelLinkStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("Null 'style' argument.");
        }
        this.labelLinkStyle = style;
    }

    /**
     * Returns the label link paint for pie charts.
     * @return The label link paint (never <code>null</code>).
     * @see #setLabelLinkPaint(Paint)
     */
    public Paint getLabelLinkPaint() {
        return this.labelLinkPaint;
    }

    /**
     * Sets the label link paint for pie charts.
     * @param paint  the paint (<code>null</code> not permitted).
     * @see #getLabelLinkPaint()
     */
    public void setLabelLinkPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.labelLinkPaint = paint;
    }

    /**
     * Returns the domain grid line paint.
     * @return The domain grid line paint (never <code>null<code>).
     * @see #setDomainGridlinePaint(Paint)
     */
    public Paint getDomainGridlinePaint() {
        return this.domainGridlinePaint;
    }

    /**
     * Sets the domain grid line paint.
     * @param paint  the paint (<code>null</code> not permitted).
     * @see #getDomainGridlinePaint()
     */
    public void setDomainGridlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.domainGridlinePaint = paint;
    }

    /**
     * Returns the range grid line paint.
     * @return The range grid line paint (never <code>null</code>).
     * @see #setRangeGridlinePaint(Paint)
     */
    public Paint getRangeGridlinePaint() {
        return this.rangeGridlinePaint;
    }

    /**
     * Sets the range grid line paint.
     * @param paint  the paint (<code>null</code> not permitted).
     * @see #getRangeGridlinePaint()
     */
    public void setRangeGridlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.rangeGridlinePaint = paint;
    }

    /**
     * Returns the crosshair paint.
     * @return The crosshair paint.
     */
    public Paint getCrosshairPaint() {
        return this.crosshairPaint;
    }

    /**
     * Sets the crosshair paint.
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setCrosshairPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.crosshairPaint = paint;
    }

    /**
     * Returns the axis offsets.
     * @return The axis offsets (never <code>null</code>).
     * @see #setAxisOffset(RectangleInsets)
     */
    public RectangleInsets getAxisOffset() {
        return this.axisOffset;
    }

    /**
     * Sets the axis offset.
     * @param offset  the offset (<code>null</code> not permitted).
     * @see #getAxisOffset()
     */
    public void setAxisOffset(RectangleInsets offset) {
        if (offset == null) {
            throw new IllegalArgumentException("Null 'offset' argument.");
        }
        this.axisOffset = offset;
    }

    /**
     * Returns the axis label paint.
     * @return The axis label paint (never <code>null</code>).
     * @see #setAxisLabelPaint(Paint)
     */
    public Paint getAxisLabelPaint() {
        return this.axisLabelPaint;
    }

    /**
     * Sets the axis label paint.
     * @param paint  the paint (<code>null</code> not permitted).
     * @see #getAxisLabelPaint()
     */
    public void setAxisLabelPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.axisLabelPaint = paint;
    }

    /**
     * Returns the tick label paint.
     * @return The tick label paint (never <code>null</code>).
     * @see #setTickLabelPaint(Paint)
     */
    public Paint getTickLabelPaint() {
        return this.tickLabelPaint;
    }

    /**
     * Sets the tick label paint.
     * @param paint  the paint (<code>null</code> not permitted).
     * @see #getTickLabelPaint()
     */
    public void setTickLabelPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.tickLabelPaint = paint;
    }

    /**
     * Returns the item label paint.
     * @return The item label paint (never <code>null</code>).
     * @see #setItemLabelPaint(Paint)
     */
    public Paint getItemLabelPaint() {
        return this.itemLabelPaint;
    }

    /**
     * Sets the item label paint.
     * @param paint  the paint (<code>null</code> not permitted).
     * @see #getItemLabelPaint()
     */
    public void setItemLabelPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.itemLabelPaint = paint;
    }

    /**
     * Returns the shadow visibility flag.
     * @return The shadow visibility flag.
     * @see #setShadowVisible(boolean)
     */
    public boolean isShadowVisible() {
        return this.shadowVisible;
    }

    /**
     * Sets the shadow visibility flag.
     * @param visible  the flag.
     * @see #isShadowVisible()
     */
    public void setShadowVisible(boolean visible) {
        this.shadowVisible = visible;
    }

    /**
     * Returns the shadow paint.
     * @return The shadow paint (never <code>null</code>).
     * @see #setShadowPaint(Paint)
     */
    public Paint getShadowPaint() {
        return this.shadowPaint;
    }

    /**
     * Sets the shadow paint.
     * @param paint  the paint (<code>null</code> not permitted).
     * @see #getShadowPaint()
     */
    public void setShadowPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.shadowPaint = paint;
    }

    /**
     * Returns the bar painter.
     * @return The bar painter (never <code>null</code>).
     * @see #setBarPainter(BarPainter)
     */
    public BarPainter getBarPainter() {
        return this.barPainter;
    }

    /**
     * Sets the bar painter.
     * @param painter  the painter (<code>null</code> not permitted).
     * @see #getBarPainter()
     */
    public void setBarPainter(BarPainter painter) {
        if (painter == null) {
            throw new IllegalArgumentException("Null 'painter' argument.");
        }
        this.barPainter = painter;
    }

    /**
     * Returns the XY bar painter.
     * @return The XY bar painter (never <code>null</code>).
     * @see #setXYBarPainter(XYBarPainter)
     */
    public XYBarPainter getXYBarPainter() {
        return this.xyBarPainter;
    }

    /**
     * Sets the XY bar painter.
     * @param painter  the painter (<code>null</code> not permitted).
     * @see #getXYBarPainter()
     */
    public void setXYBarPainter(XYBarPainter painter) {
        if (painter == null) {
            throw new IllegalArgumentException("Null 'painter' argument.");
        }
        this.xyBarPainter = painter;
    }

    /**
     * Returns the thermometer paint.
     * @return The thermometer paint (never <code>null</code>).
     * @see #setThermometerPaint(Paint)
     */
    public Paint getThermometerPaint() {
        return this.thermometerPaint;
    }

    /**
     * Sets the thermometer paint.
     * @param paint  the paint (<code>null</code> not permitted).
     * @see #getThermometerPaint()
     */
    public void setThermometerPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.thermometerPaint = paint;
    }

    /**
     * Returns the wall paint for charts with a 3D effect.
     * @return The wall paint (never <code>null</code>).
     * @see #setWallPaint(Paint)
     */
    public Paint getWallPaint() {
        return this.wallPaint;
    }

    /**
     * Sets the wall paint for charts with a 3D effect.
     * @param paint  the paint (<code>null</code> not permitted).
     * @see #getWallPaint()
     */
    public void setWallPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.wallPaint = paint;
    }

    /**
     * Returns the error indicator paint.
     * @return The error indicator paint (never <code>null</code>).
     * @see #setErrorIndicatorPaint(Paint)
     */
    public Paint getErrorIndicatorPaint() {
        return this.errorIndicatorPaint;
    }

    /**
     * Sets the error indicator paint.
     * @param paint  the paint (<code>null</code> not permitted).
     * @see #getErrorIndicatorPaint()
     */
    public void setErrorIndicatorPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.errorIndicatorPaint = paint;
    }

    /**
     * Returns the grid band paint.
     *
     * @return The grid band paint (never <code>null</code>).
     *
     * @see #setGridBandPaint(Paint)
     */
    public Paint getGridBandPaint() {
        return this.gridBandPaint;
    }

    /**
     * Sets the grid band paint.
     * @param paint  the paint (<code>null</code> not permitted).
     * @see #getGridBandPaint()
     */
    public void setGridBandPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.gridBandPaint = paint;
    }

    /**
     * Returns the grid band alternate paint (used for a {@link SymbolAxis}).
     * @return The paint (never <code>null</code>).
     * @see #setGridBandAlternatePaint(Paint)
     */
    public Paint getGridBandAlternatePaint() {
        return this.gridBandAlternatePaint;
    }

    /**
     * Sets the grid band alternate paint (used for a {@link SymbolAxis}).
     * @param paint  the paint (<code>null</code> not permitted).
     * @see #getGridBandAlternatePaint()
     */
    public void setGridBandAlternatePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.gridBandAlternatePaint = paint;
    }

    /**
     * Returns a clone of the drawing supplier for this theme.
     * @return A clone of the drawing supplier.
     */
    public DrawingSupplier getDrawingSupplier() {
        DrawingSupplier result = null;
        if (this.drawingSupplier instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.drawingSupplier;
              try {
                result = (DrawingSupplier) pc.clone();
            }
            catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Sets the drawing supplier for this theme.
     * @param supplier  the supplier (<code>null</code> not permitted).
     * @see #getDrawingSupplier()
     */
    public void setDrawingSupplier(DrawingSupplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Null 'supplier' argument.");
        }
        this.drawingSupplier = supplier;
    }

    /**
     * Applies this theme to the supplied chart.
     * @param chart  the chart (<code>null</code> not permitted).
     */
    public void apply(JFreeChart chart) {
        if (chart == null) {
            throw new IllegalArgumentException("Null 'chart' argument.");
        }
        TextTitle title = chart.getTitle();
        if (title != null) {
            title.setFont(this.extraLargeFont);
            title.setPaint(this.titlePaint);
        }

        int subtitleCount = chart.getSubtitleCount();
        for (int i = 0; i < subtitleCount; i++) {
            applyToTitle(chart.getSubtitle(i));
        }

        chart.setBackgroundPaint(this.chartBackgroundPaint);

        // now process the plot if there is one
        Plot plot = chart.getPlot();
        if (plot != null) {
            applyToPlot(plot);
        }
    }

    /**
     * Applies the attributes of this theme to the specified title.
     * @param title  the title.
     */
    protected void applyToTitle(Title title) {
        if (title instanceof TextTitle) {
            TextTitle tt = (TextTitle) title;
            tt.setFont(this.largeFont);
            tt.setPaint(this.subtitlePaint);
        }
        else if (title instanceof LegendTitle) {
            LegendTitle lt = (LegendTitle) title;
            if (lt.getBackgroundPaint() != null) {
                lt.setBackgroundPaint(this.legendBackgroundPaint);
            }
            lt.setItemFont(this.regularFont);
            lt.setItemPaint(this.legendItemPaint);
            if (lt.getWrapper() != null) {
                applyToBlockContainer(lt.getWrapper());
            }
        }
        else if (title instanceof PaintScaleLegend) {
            PaintScaleLegend psl = (PaintScaleLegend) title;
            psl.setBackgroundPaint(this.legendBackgroundPaint);
            ValueAxis axis = psl.getAxis();
            if (axis != null) {
                applyToValueAxis(axis);
            }
        }
        else if (title instanceof CompositeTitle) {
            CompositeTitle ct = (CompositeTitle) title;
            BlockContainer bc = ct.getContainer();
            List blocks = bc.getBlocks();
            Iterator iterator = blocks.iterator();
            while (iterator.hasNext()) {
                Block b = (Block) iterator.next();
                if (b instanceof Title) {
                    applyToTitle((Title) b);
                }
            }
        }
    }

    /**
     * Applies the attributes of this theme to the specified container.
     * @param bc  a block container (<code>null</code> not permitted).
     */
    protected void applyToBlockContainer(BlockContainer bc) {
        Iterator iterator = bc.getBlocks().iterator();
        while (iterator.hasNext()) {
            Block b = (Block) iterator.next();
            applyToBlock(b);
        }
    }

    /**
     * Applies the attributes of this theme to the specified block.
     * @param b  the block.
     */
    protected void applyToBlock(Block b) {
        if (b instanceof Title) {
            applyToTitle((Title) b);
        }
        else if (b instanceof LabelBlock) {
            LabelBlock lb = (LabelBlock) b;
            lb.setFont(this.regularFont);
            lb.setPaint(this.legendItemPaint);
        }
    }

    /**
     * Applies the attributes of this theme to a plot.
     * @param plot  the plot (<code>null</code>).
     */
    protected void applyToPlot(Plot plot) {
        if (plot == null) {
            throw new IllegalArgumentException("Null 'plot' argument.");
        }
        if (plot.getDrawingSupplier() != null) {
            plot.setDrawingSupplier(getDrawingSupplier());
        }
        if (plot.getBackgroundPaint() != null) {
            plot.setBackgroundPaint(this.plotBackgroundPaint);
        }
        plot.setOutlinePaint(this.plotOutlinePaint);

        // now handle specific plot types (and yes, I know this is some
        // really ugly code that has to be manually updated any time a new
        // plot type is added - I should have written something much cooler,
        // but I didn't and neither did anyone else).
        if (plot instanceof PiePlot) {
            applyToPiePlot((PiePlot) plot);
        }
        else if (plot instanceof MultiplePiePlot) {
            applyToMultiplePiePlot((MultiplePiePlot) plot);
        }
        else if (plot instanceof CategoryPlot) {
            applyToCategoryPlot((CategoryPlot) plot);
        }
        else if (plot instanceof XYPlot) {
            applyToXYPlot((XYPlot) plot);
        }
        else if (plot instanceof FastScatterPlot) {
            applyToFastScatterPlot((FastScatterPlot) plot);
        }
        else if (plot instanceof MeterPlot) {
            applyToMeterPlot((MeterPlot) plot);
        }
        else if (plot instanceof ThermometerPlot) {
            applyToThermometerPlot((ThermometerPlot) plot);
        }
        else if (plot instanceof SpiderWebPlot) {
            applyToSpiderWebPlot((SpiderWebPlot) plot);
        }
        else if (plot instanceof PolarPlot) {
            applyToPolarPlot((PolarPlot) plot);
        }
    }

    /**
     * Applies the attributes of this theme to a {@link PiePlot} instance.
     * This method also clears any set values for the section paint, outline
     * etc, so that the theme's {@link DrawingSupplier} will be used.
     * @param plot  the plot (<code>null</code> not permitted).
     */
    protected void applyToPiePlot(PiePlot plot) {
        plot.setLabelLinkPaint(this.labelLinkPaint);
        plot.setLabelLinkStyle(this.labelLinkStyle);
        plot.setLabelFont(this.regularFont);
        plot.setShadowPaint(this.pieShadowPaint);
        plot.setBaseSectionOutlinePaint(Color.WHITE); // Make the pie segement outlines white
        plot.setOutlineVisible(false); // Don't display a border around the pie plot part.

        // clear the section attributes so that the theme's DrawingSupplier will be used
        if (plot.getAutoPopulateSectionPaint()) {
            plot.clearSectionPaints(false);
        }
        if (plot.getAutoPopulateSectionOutlineStroke()) {
            plot.clearSectionOutlineStrokes(false);
        }
    }

    /**
     * Applies the attributes of this theme to a {@link MultiplePiePlot}.
     * @param plot  the plot (<code>null</code> not permitted).
     */
    protected void applyToMultiplePiePlot(MultiplePiePlot plot) {
        apply(plot.getPieChart());
    }

    /**
     * Applies the attributes of this theme to a {@link CategoryPlot}.
     * @param plot  the plot (<code>null</code> not permitted).
     */
    protected void applyToCategoryPlot(CategoryPlot plot) {
        plot.setAxisOffset(this.axisOffset);
        plot.setDomainGridlinePaint(this.domainGridlinePaint);
        plot.setRangeGridlinePaint(this.rangeGridlinePaint);

        // process all domain axes
        int domainAxisCount = plot.getDomainAxisCount();
        for (int i = 0; i < domainAxisCount; i++) {
            CategoryAxis axis = (CategoryAxis) plot.getDomainAxis(i);
            if (axis != null) {
                applyToCategoryAxis(axis);
            }
        }

        // process all range axes
        int rangeAxisCount = plot.getRangeAxisCount();
        for (int i = 0; i < rangeAxisCount; i++) {
            ValueAxis axis = (ValueAxis) plot.getRangeAxis(i);
            if (axis != null) {
                applyToValueAxis(axis);
            }
        }

        // process all renderers
        int rendererCount = plot.getRendererCount();
        for (int i = 0; i < rendererCount; i++) {
            CategoryItemRenderer r = plot.getRenderer(i);
            if (r != null) {
                applyToCategoryItemRenderer(r);
            }
        }

        if (plot instanceof CombinedDomainCategoryPlot) {
            CombinedDomainCategoryPlot cp = (CombinedDomainCategoryPlot) plot;
            Iterator iterator = cp.getSubplots().iterator();
            while (iterator.hasNext()) {
                CategoryPlot subplot = (CategoryPlot) iterator.next();
                if (subplot != null) {
                    applyToPlot(subplot);
                }
            }
        }
        if (plot instanceof CombinedRangeCategoryPlot) {
            CombinedRangeCategoryPlot cp = (CombinedRangeCategoryPlot) plot;
            Iterator iterator = cp.getSubplots().iterator();
            while (iterator.hasNext()) {
                CategoryPlot subplot = (CategoryPlot) iterator.next();
                if (subplot != null) {
                    applyToPlot(subplot);
                }
            }
        }
    }

    /**
     * Applies the attributes of this theme to a {@link XYPlot}.
     * @param plot  the plot (<code>null</code> not permitted).
     */
    protected void applyToXYPlot(XYPlot plot) {
        plot.setAxisOffset(this.axisOffset);
        plot.setDomainGridlinePaint(this.domainGridlinePaint);
        plot.setRangeGridlinePaint(this.rangeGridlinePaint);
        plot.setDomainCrosshairPaint(this.crosshairPaint);
        plot.setRangeCrosshairPaint(this.crosshairPaint);
        // process all domain axes
        int domainAxisCount = plot.getDomainAxisCount();
        for (int i = 0; i < domainAxisCount; i++) {
            ValueAxis axis = (ValueAxis) plot.getDomainAxis(i);
            if (axis != null) {
                applyToValueAxis(axis);
            }
        }

        // process all range axes
        int rangeAxisCount = plot.getRangeAxisCount();
        for (int i = 0; i < rangeAxisCount; i++) {
            ValueAxis axis = (ValueAxis) plot.getRangeAxis(i);
            if (axis != null) {
                applyToValueAxis(axis);
            }
        }

        // process all renderers
        int rendererCount = plot.getRendererCount();
        for (int i = 0; i < rendererCount; i++) {
            XYItemRenderer r = plot.getRenderer(i);
            if (r != null) {
                applyToXYItemRenderer(r);
            }
        }

        // process all annotations
        Iterator iter = plot.getAnnotations().iterator();
        while (iter.hasNext()) {
            XYAnnotation a = (XYAnnotation) iter.next();
            applyToXYAnnotation(a);
        }

        if (plot instanceof CombinedDomainXYPlot) {
            CombinedDomainXYPlot cp = (CombinedDomainXYPlot) plot;
            Iterator iterator = cp.getSubplots().iterator();
            while (iterator.hasNext()) {
                XYPlot subplot = (XYPlot) iterator.next();
                if (subplot != null) {
                    applyToPlot(subplot);
                }
            }
        }
        if (plot instanceof CombinedRangeXYPlot) {
            CombinedRangeXYPlot cp = (CombinedRangeXYPlot) plot;
            Iterator iterator = cp.getSubplots().iterator();
            while (iterator.hasNext()) {
                XYPlot subplot = (XYPlot) iterator.next();
                if (subplot != null) {
                    applyToPlot(subplot);
                }
            }
        }
    }

    /**
     * Applies the attributes of this theme to a {@link FastScatterPlot}.
     * @param plot
     */
    protected void applyToFastScatterPlot(FastScatterPlot plot) {
        plot.setDomainGridlinePaint(this.domainGridlinePaint);
        plot.setRangeGridlinePaint(this.rangeGridlinePaint);
        ValueAxis xAxis = plot.getDomainAxis();
        if (xAxis != null) {
            applyToValueAxis(xAxis);
        }
        ValueAxis yAxis = plot.getRangeAxis();
        if (yAxis != null) {
            applyToValueAxis(yAxis);
        }

    }

    /**
     * Applies the attributes of this theme to a {@link PolarPlot}.  This
     * method is called from the {@link #applyToPlot(Plot)} method.
     * @param plot  the plot (<code>null</code> not permitted).
     */
    protected void applyToPolarPlot(PolarPlot plot) {
        plot.setAngleLabelFont(this.regularFont);
        plot.setAngleLabelPaint(this.tickLabelPaint);
        plot.setAngleGridlinePaint(this.domainGridlinePaint);
        plot.setRadiusGridlinePaint(this.rangeGridlinePaint);
        ValueAxis axis = plot.getAxis();
        if (axis != null) {
            applyToValueAxis(axis);
        }
    }

    /**
     * Applies the attributes of this theme to a {@link SpiderWebPlot}.
     * @param plot  the plot (<code>null</code> not permitted).
     */
    protected void applyToSpiderWebPlot(SpiderWebPlot plot) {
        plot.setLabelFont(this.regularFont);
        plot.setLabelPaint(this.axisLabelPaint);
        plot.setAxisLinePaint(this.axisLabelPaint);
    }

    /**
     * Applies the attributes of this theme to a {@link MeterPlot}.
     * @param plot  the plot (<code>null</code> not permitted).
     */
    protected void applyToMeterPlot(MeterPlot plot) {
        plot.setDialBackgroundPaint(this.plotBackgroundPaint);
        plot.setValueFont(this.largeFont);
        plot.setValuePaint(this.axisLabelPaint);
        plot.setDialOutlinePaint(this.plotOutlinePaint);
        plot.setNeedlePaint(this.thermometerPaint);
        plot.setTickLabelFont(this.regularFont);
        plot.setTickLabelPaint(this.tickLabelPaint);
    }

    /**
     * Applies the attributes for this theme to a {@link ThermometerPlot}.
     * This method is called from the {@link #applyToPlot(Plot)} method.
     *
     * @param plot  the plot.
     */
    protected void applyToThermometerPlot(ThermometerPlot plot) {
        plot.setValueFont(this.largeFont);
        plot.setThermometerPaint(this.thermometerPaint);
        ValueAxis axis = plot.getRangeAxis();
        if (axis != null) {
            applyToValueAxis(axis);
        }
    }

    /**
     * Applies the attributes for this theme to a {@link CategoryAxis}.
     *
     * @param axis  the axis (<code>null</code> not permitted).
     */
    protected void applyToCategoryAxis(CategoryAxis axis) {
        axis.setLabelFont(this.largeFont);
        axis.setLabelPaint(this.axisLabelPaint);
        axis.setTickLabelFont(this.regularFont);
        axis.setTickLabelPaint(this.tickLabelPaint);
        if (axis instanceof SubCategoryAxis) {
            SubCategoryAxis sca = (SubCategoryAxis) axis;
            sca.setSubLabelFont(this.regularFont);
            sca.setSubLabelPaint(this.tickLabelPaint);
        }
    }

    /**
     * Applies the attributes for this theme to a {@link ValueAxis}.
     *
     * @param axis  the axis (<code>null</code> not permitted).
     */
    protected void applyToValueAxis(ValueAxis axis) {
        axis.setLabelFont(this.largeFont);
        axis.setLabelPaint(this.axisLabelPaint);
        axis.setTickLabelFont(this.regularFont);
        axis.setTickLabelPaint(this.tickLabelPaint);
        if (axis instanceof SymbolAxis) {
            applyToSymbolAxis((SymbolAxis) axis);
        }
        if (axis instanceof PeriodAxis) {
            applyToPeriodAxis((PeriodAxis) axis);
        }
    }

    /**
     * Applies the attributes for this theme to a {@link SymbolAxis}.
     * @param axis  the axis (<code>null</code> not permitted).
     */
    protected void applyToSymbolAxis(SymbolAxis axis) {
        axis.setGridBandPaint(this.gridBandPaint);
        axis.setGridBandAlternatePaint(this.gridBandAlternatePaint);
    }

    /**
     * Applies the attributes for this theme to a {@link PeriodAxis}.
     * @param axis  the axis (<code>null</code> not permitted).
     */
    protected void applyToPeriodAxis(PeriodAxis axis) {
        PeriodAxisLabelInfo[] info = axis.getLabelInfo();
        for (int i = 0; i < info.length; i++) {
            PeriodAxisLabelInfo e = info[i];
            PeriodAxisLabelInfo n = new PeriodAxisLabelInfo(e.getPeriodClass(),
                    e.getDateFormat(), e.getPadding(), this.regularFont,
                    this.tickLabelPaint, e.getDrawDividers(),
                    e.getDividerStroke(), e.getDividerPaint());
            info[i] = n;
        }
        axis.setLabelInfo(info);
    }

    /**
     * Applies the attributes for this theme to an {@link AbstractRenderer}.
     * @param renderer  the renderer (<code>null</code> not permitted).
     */
    protected void applyToAbstractRenderer(AbstractRenderer renderer) {
        if (renderer.getAutoPopulateSeriesPaint()) {
            renderer.clearSeriesPaints(false);
        }
        if (renderer.getAutoPopulateSeriesStroke()) {
            renderer.clearSeriesStrokes(false);
        }
    }

    /**
     * Applies the settings of this theme to the specified renderer.
     * @param renderer  the renderer (<code>null</code> not permitted).
     */
    protected void applyToCategoryItemRenderer(CategoryItemRenderer renderer) {
        if (renderer == null) {
            throw new IllegalArgumentException("Null 'renderer' argument.");
        }

        if (renderer instanceof AbstractRenderer) {
            applyToAbstractRenderer((AbstractRenderer) renderer);
        }

        renderer.setBaseItemLabelFont(this.regularFont);
        renderer.setBaseItemLabelPaint(this.itemLabelPaint);

        // now we handle some special cases - yes, UGLY code alert!
        
        // BarRenderer
        if (renderer instanceof BarRenderer) {
            BarRenderer br = (BarRenderer) renderer;
            br.setBarPainter(this.barPainter);
            br.setShadowVisible(this.shadowVisible);
            br.setShadowPaint(this.shadowPaint);
            br.setDrawBarOutline(this.outlineVisible);
            
            // Make the bars transparent. Only the first one done here
            // Because setting 20 means that lines and bars use the same colours - not sure why yet.
        	for (int seriesIndex = 0; seriesIndex < 1; seriesIndex++) {
            	Color c = (Color)br.lookupSeriesPaint(seriesIndex);
            	Color d = new Color(c.getRed(),c.getGreen(),c.getBlue(),80); // Make it transparent
            	br.setSeriesPaint(seriesIndex, d);
        	}
        }

        // BarRenderer3D
        if (renderer instanceof BarRenderer3D) {
            BarRenderer3D br3d = (BarRenderer3D) renderer;
            br3d.setWallPaint(this.wallPaint);
        }

        // LineRenderer3D
        if (renderer instanceof LineRenderer3D) {
            LineRenderer3D lr3d = (LineRenderer3D) renderer;
            lr3d.setWallPaint(this.wallPaint);
        }

        //  StatisticalBarRenderer
        if (renderer instanceof StatisticalBarRenderer) {
            StatisticalBarRenderer sbr = (StatisticalBarRenderer) renderer;
            sbr.setErrorIndicatorPaint(this.errorIndicatorPaint);
        }

        // MinMaxCategoryRenderer
        if (renderer instanceof MinMaxCategoryRenderer) {
            MinMaxCategoryRenderer mmcr = (MinMaxCategoryRenderer) renderer;
            mmcr.setGroupPaint(this.errorIndicatorPaint);
        }
    }

    /**
     * Applies the settings of this theme to the specified renderer.
     * @param renderer  the renderer (<code>null</code> not permitted).
     */
    protected void applyToXYItemRenderer(XYItemRenderer renderer) {
        if (renderer == null) {
            throw new IllegalArgumentException("Null 'renderer' argument.");
        }
        if (renderer instanceof AbstractRenderer) {
            applyToAbstractRenderer((AbstractRenderer) renderer);
        }
        renderer.setBaseItemLabelFont(this.regularFont);
        renderer.setBaseItemLabelPaint(this.itemLabelPaint);
        if (renderer instanceof XYBarRenderer) {
            XYBarRenderer br = (XYBarRenderer) renderer;
            br.setBarPainter(this.xyBarPainter);
            br.setShadowVisible(this.shadowVisible);
        }
    }

    /**
     * Applies the settings of this theme to the specified annotation.
     * @param annotation  the annotation.
     */
    protected void applyToXYAnnotation(XYAnnotation annotation) {
        if (annotation == null) {
            throw new IllegalArgumentException("Null 'annotation' argument.");
        }
        if (annotation instanceof XYTextAnnotation) {
            XYTextAnnotation xyta = (XYTextAnnotation) annotation;
            xyta.setFont(this.smallFont);
            xyta.setPaint(this.itemLabelPaint);
        }
    }

}
