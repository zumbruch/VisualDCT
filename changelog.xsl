<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:template match="/changelog">
    <HTML>
      <HEAD>
         <TITLE>ChangeLog: <xsl:value-of select="@project"/> - <xsl:value-of select="@description"/></TITLE>
      </HEAD>
      <BODY>

        <xsl:element name="font">
            <xsl:attribute name="size">+2</xsl:attribute>
	        <xsl:element name="b">
    	    	<xsl:value-of select="@project"/>
	        </xsl:element>
        </xsl:element>
        
        <xsl:element name="br"/>
        
       	<xsl:value-of select="@description"/>
        
        <p><font size="-1">See a 
          <xsl:element name="a">
            <xsl:attribute name="href">#developer-list</xsl:attribute>
            <xsl:text>list of developers/initials.</xsl:text>
          </xsl:element>
        </font></p>
        
          <xsl:for-each select="version">
	        <xsl:element name="hr"/>
	        <xsl:element name="table">
	            <xsl:attribute name="border">0</xsl:attribute>
	            <xsl:attribute name="width">100%</xsl:attribute>
	            <xsl:attribute name="cellspacing">2</xsl:attribute>
	            <xsl:attribute name="cellpadding">3</xsl:attribute>
   				<xsl:attribute name="bgcolor">#000000</xsl:attribute>
	            
		        <xsl:element name="tr">
       				<xsl:attribute name="bgcolor">#bbbbff</xsl:attribute>
			        <xsl:element name="td">
       				<xsl:attribute name="align">right</xsl:attribute>
		   				<xsl:attribute name="width">10%</xsl:attribute>
				        <xsl:element name="font">
            				<xsl:attribute name="size">+2</xsl:attribute>
							v<xsl:value-of select="@ver"/> 
				        </xsl:element>
			        </xsl:element>
			        <xsl:element name="td">
		   				<xsl:attribute name="width">5%</xsl:attribute>
	       				<xsl:attribute name="align">center</xsl:attribute>
          				<xsl:element name="a">
	            			<xsl:attribute name="href">#personref-<xsl:value-of select="@who"/></xsl:attribute>
    	        			<xsl:value-of select="@who"/>
        	  			</xsl:element>
			        </xsl:element>	
        	  			
			        <xsl:element name="td">
				        <xsl:element name="font">
            				<xsl:attribute name="size">+2</xsl:attribute>
    	   					build <xsl:value-of select="@build"/>,
	        	  			released <xsl:value-of select="@date"/>
				        </xsl:element>
			        </xsl:element>	
			        
		        </xsl:element>	
		        
                <xsl:apply-templates/>
               
	        </xsl:element>
          </xsl:for-each>
          
         <!--xsl:when test="name(.)='who'">
          <xsl:element name="a">
            <xsl:attribute name="href">#personref-<xsl:value-of select="."/></xsl:attribute>
            <xsl:value-of select="."/>
          </xsl:element>
        </xsl:when-->

        <xsl:element name="hr"/>
        
        <xsl:call-template name="developer-list"/>

      </BODY>
    </HTML>
  </xsl:template>

  <xsl:template match="fix">
  	<xsl:element name="tr">
		<xsl:attribute name="bgcolor">#bbffbb</xsl:attribute>
	  	<xsl:element name="td">
			<xsl:attribute name="align">right</xsl:attribute>
	  		<xsl:value-of select="name(.)"/>
	  	</xsl:element>
	  	
	  	<xsl:element name="td">
			<xsl:attribute name="align">center</xsl:attribute>
			<xsl:element name="a">
            	<xsl:attribute name="href">#personref-<xsl:value-of select="@who"/></xsl:attribute>
            	<xsl:value-of select="@who"/>
          	</xsl:element>
	  	</xsl:element>

	  	<xsl:element name="td">
	  		<xsl:value-of select="."/>
	  	</xsl:element>
  	</xsl:element>
  </xsl:template>

  <xsl:template match="enhancement">
  	<xsl:element name="tr">
		<xsl:attribute name="bgcolor">#bbffff</xsl:attribute>
	  	<xsl:element name="td">
			<xsl:attribute name="align">right</xsl:attribute>
	  		<xsl:value-of select="name(.)"/>
	  	</xsl:element>
	  	
	  	<xsl:element name="td">
			<xsl:attribute name="align">center</xsl:attribute>
			<xsl:element name="a">
            	<xsl:attribute name="href">#personref-<xsl:value-of select="@who"/></xsl:attribute>
            	<xsl:value-of select="@who"/>
          	</xsl:element>
	  	</xsl:element>

	  	<xsl:element name="td">
	  		<xsl:value-of select="."/>
	  	</xsl:element>
  	</xsl:element>
  </xsl:template>

  <xsl:template match="comment">
  	<xsl:element name="tr">
		<xsl:attribute name="bgcolor">#ffffff</xsl:attribute>
	  	<xsl:element name="td">
			<xsl:attribute name="align">right</xsl:attribute>
	  		<xsl:value-of select="name(.)"/>
	  	</xsl:element>
	  	
	  	<xsl:element name="td">
			<xsl:attribute name="align">center</xsl:attribute>
			<xsl:element name="a">
            	<xsl:attribute name="href">#personref-<xsl:value-of select="@who"/></xsl:attribute>
            	<xsl:value-of select="@who"/>
          	</xsl:element>
	  	</xsl:element>

	  	<xsl:element name="td">
	  		<xsl:value-of select="."/>
	  	</xsl:element>
  	</xsl:element>
  </xsl:template>

  <xsl:template match="bug">
  	<xsl:element name="tr">
		<xsl:attribute name="bgcolor">#ffbbbb</xsl:attribute>
	  	<xsl:element name="td">
			<xsl:attribute name="align">right</xsl:attribute>
	  		<xsl:value-of select="name(.)"/>
	  	</xsl:element>
	  	
	  	<xsl:element name="td">
			<xsl:element name="a">
				<xsl:attribute name="align">center</xsl:attribute>
            	<xsl:attribute name="href">#personref-<xsl:value-of select="@who"/></xsl:attribute>
            	<xsl:value-of select="@who"/>
          	</xsl:element>
	  	</xsl:element>

	  	<xsl:element name="td">
	  		<xsl:value-of select="."/>
	  	</xsl:element>
  	</xsl:element>
  </xsl:template>

  <xsl:template name="developer-list">
    <H3>
      <xsl:element name="a">
        <xsl:attribute name="name">developer-list</xsl:attribute>
        <xsl:text>Developers:</xsl:text>
      </xsl:element>
    </H3>
    <p>A list of some of people currently working on working on <xsl:value-of select="@project"/>:</p>
    <ul>
    <xsl:for-each select="devs/person">
      <li>
        <a href="mailto:{@email}">
          <xsl:value-of select="@name"/>
        </a>
         <xsl:element name="a">
           <xsl:attribute name="name"><xsl:text>personref-</xsl:text><xsl:value-of select="@id"/></xsl:attribute>
           <xsl:text> (</xsl:text><xsl:value-of select="@id"/><xsl:text>)</xsl:text>
         </xsl:element>
         <BR/><xsl:value-of select="."/><BR/><BR/>
      </li>
    </xsl:for-each>
    </ul>
  </xsl:template>

</xsl:stylesheet>
